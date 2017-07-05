#!/usr/bin/python

import socket
import threading
import time
import json

TIME_BETWEEN        = 0.5
TIME_WAIT_CLIENT    = 5
MAXPLAYER			= 4

class Player(threading.Thread):
	def __init__(self, conn, user, game_id, game):
		threading.Thread.__init__(self)

		self.ready    = threading.Event()
		self.nick     = "Bot"
		self.running  = True
		self.alive    = True
		self.conn     = conn
		self.user_id  = user
		self.game_id  = game_id
		self.game     = game

		self.ready.clear()

		print("Created user", self.user_id, "of game", self.game_id)

	def send_to_client(self, msg):
		try:
			self.conn.send( msg.encode() )
			# Clients needs a end of line
			self.conn.send( b"\n" )
		except:
			self.alive = False

	def get_id(self):
		return self.user_id

	def get_name(self):
		return self.nick

	def wait(self, timeout = None):
		return self.ready.wait(timeout)

	def run(self):
		self.conn.settimeout(0.5)
		
		while self.running == True and self.alive == True:
			try:
				data = self.conn.recv(1024)
				self.process_data(data)
			except:
				pass
		
		# Send broadcast to remove
		if self.ready.is_set():
			self.game.remove_player(self)
		
		print("End thread")
		if self.alive: 
			self.conn.shutdown(socket.SHUT_RDWR)
			self.conn.close()

	def send_cmd(self, cmd, value = None):
		data = {}
		data["cmd"]   = cmd

		if value != None:
			data["value"] = value

		self.send_to_client( json.dumps(data) )

	def process_data(self, data):
		json_data = json.loads(data)

		# Receive nickname of client
		if 'nickname' in json_data:
			self.nick  = json_data['nickname']
			self.ready.set()
			print(self.nick, "has entered into game")
		
		if 'cmd' in json_data:
			cmd    = json_data['cmd']
			# Start game
			if cmd == 'start_game':
				self.game.start_game()
			if cmd == 'disconnect':
				self.running = False
			if cmd == 'ack':
				value  = json_data['value']
				self.game.ack_recv(self.user_id, value)
			if cmd == 'card':
				value  = json_data['value'].replace('[', '').replace(']', '')
				values = value.split(',')
				
				self.game.card_player([self.user_id, int(values[0]), int(values[1])])
		

class Game(threading.Thread):

	def __init__(self, game):
		threading.Thread.__init__(self)

		self.running            = True
		self.c_user             = 0
		self.game_id 			= game
		self.players            = []
		self.status             = 'waiting'
		self.ack_tt             = []
		self.ack_t              = []
		self.ack_st             = []
		self.ack_sst            = []
		self.card_st            = []
		self.subSubTurnCounter  = 0
		self.state              = 0

	# Register a new player in game
	def register_player(self, conn):
		p = Player(conn, self.c_user, self.game_id, self)
		p.start()
		
		# Check initial messages from client
		if not p.wait(TIME_WAIT_CLIENT):
			print ('Error adding client')
			p.running = False
			del p
			return

		# Send new player data to other clients
		self.send_broadcast('new_player', [p.get_id(), self.game_id, p.get_name()])
		p.send_cmd('info_player', [p.get_id(), p.get_name()])
		time.sleep(TIME_BETWEEN)

		# Append new client to game
		self.c_user = self.c_user + 1
		self.players.append(p)
		# Send all players to him
		for op in self.players:
			p.send_cmd('new_player', [op.get_id(), self.game_id, op.get_name()])
			time.sleep(TIME_BETWEEN)

	# Remove player of list
	def remove_player(self, player):
		print('Deleting user', player.get_name())
		self.send_broadcast('del_player', [player.get_id(), self.game_id])
		self.players.remove(player)

	# Get number of players
	def get_players(self):
		return len(self.players)

	# Get status of current game
	def get_status(self):
		return self.status

	# Send a message to all clients
	def send_broadcast(self, cmd, value = None):
		for p in self.players:
			print(p.get_id(), p.get_name(), p.game_id)
			p.send_cmd(cmd, value)
			time.sleep(TIME_BETWEEN)

	# Start game
	def start_game(self):
		self.status = 'starting'
		print('Starting game')
		self.send_broadcast('start_game')
		# 5 seconds countdown 
		time.sleep(5)
		print('Play turn one')
		self.status = 'playing'

	def ack_recv(self, user_id, type):
		if type == 'TIMEOUT_ORDER':
			self.ack_tt.append(user_id)
		if type == 'TURN':
			# Turn ACK
			self.ack_t.append(user_id)
		if type == 'SUBTURN':
			# Subturn ACK
			self.ack_st.append(user_id)
		if type == 'SUBSUBTURN' or type == 'WIN':
			# Subsubturn ACK
			self.ack_sst.append(user_id)

	def card_player(self, data):
		# Append received card to list
		self.card_st.append(data)

	def orderCards(self):
		# Sort cards of subturn
		self.card_st.sort(key=lambda x: x[2], reverse=True)
	def checkSubTurnState(self):
		# Check if some player wins
		if 'WIN' is self.ack_sst:
			return True
		return False

	def run(self):
		while self.running == True:
			time.sleep(TIME_BETWEEN)

			# Wait for start of game
			if self.state == 0:
				if self.status == 'playing':
					self.state = 1
			# Send new turn
			elif self.state == 1:
					self.send_broadcast('new_turn')
					self.state = 2
			# Turn timeout ACK
			elif self.state == 2:
				# Wait ack of all user
				if len(self.ack_tt) == len(self.players):
					self.ack_tt.clear()
					self.state = 3
			# Send new subturn
			elif self.state == 3:
				self.send_broadcast('new_subturn')
				self.state = 4
			# Subturn state Card
			elif self.state == 4:
				# Wait card of all user
				if len(self.card_st) == len(self.players):
					self.orderCards()
					self.state = 5
			# Send new subsubturn
			elif self.state == 5:
				if self.subSubTurnCounter > len(self.card_st):
					print("Error")
				else:
					self.send_broadcast('new_subsubturn', self.card_st[self.subSubTurnCounter])
				self.state = 6
			# Check turns ACK
			elif self.state == 6:
				# Subsubturn ACK
				if len(self.ack_sst) == len(self.players):
					# Clear data
					self.ack_sst.clear()
					# Increase counter subsubturn
					self.subSubTurnCounter = self.subSubTurnCounter + 1
					# Go to send subsubturn
					self.state = 5
				# Subturn ACK
				if len(self.ack_st) == len(self.players):
					# Clear data
					self.ack_sst.clear()
					self.card_st.clear()
					self.ack_st.clear()
					self.subSubTurnCounter = 0
					# Go to send subturn
					self.state = 3

				# Turn ACK
				if len(self.ack_t) == len(self.players):
					# Clear data
					self.ack_sst.clear()
					self.card_st.clear()
					self.ack_st.clear()
					self.ack_t.clear()
					self.subSubTurnCounter = 0
					# Go to send turn
					self.state = 1

				# One player wins
				if self.checkSubTurnState():
					print('Some player wins')
					time.sleep(3)
					break;

		# Close sockets in players
		while len(self.players) > 0:
			self.players[0].running = False
			time.sleep(TIME_BETWEEN)
			self.players[0].join()
		
class Server:

	def __init__(self, host, port):
		self.c_game  = 0
		self.games   = []

		print('RoboRally server')
		self.s = socket.socket()            # Create a socket object
		# Solve problem with restart server
		self.s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		# 
		self.s.bind( (host, port) )         # Bind to the port
		self.s.listen(5)                    # Now wait for client connection.

	def listen(self):
		print('Waiting for clients...')

		try:
			while True:
				c, addr = self.s.accept()   # Establish connection with client.
				
				try:
					# Create a game
					if self.c_game == 0 or self.games[-1].get_status() != 'waiting' or self.games[-1].get_players() >= MAXPLAYER:
						print('Creating a new game')
						self.games.append( Game(self.c_game) )
						self.games[-1].start()
						self.c_game = self.c_game + 1

					self.games[-1].register_player(c)

				except:
					c.close()
	
		except KeyboardInterrupt:
			pass
		
		while len(self.games) > 0:
			self.games[0].running = False
			time.sleep(TIME_BETWEEN)
			self.games[0].join()

			del self.games[0]

		self.s.close()


s    = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.connect(('8.8.8.8', 80))
host = s.getsockname()[0]
s.close()

print('Connect to', host)
port = 5000

s = Server(host, port)
s.listen()
