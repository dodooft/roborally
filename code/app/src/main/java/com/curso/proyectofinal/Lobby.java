package com.curso.proyectofinal;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jsoto on 30-06-17.
 */

public class Lobby {
    private LinearLayout.LayoutParams paramsCard;
    private List<Player> playersOnGame;
    private List<LinearLayout> playerCards;
    private LinearLayout playerListLay;

    private TextView playerNum, playerNick;
    private Integer current_id;
    private String current_nick;
    private Context context;
    private ImageView smallIcon;
    private LinearLayout LobbyLayout;

    final public int[] tokenImages = {R.drawable.tkbender, R.drawable.tkr2d2, R.drawable.tkwalle, R.drawable.tkmegaman};

    public Lobby(Context context) {

        this.LobbyLayout   = (LinearLayout) LinearLayout.inflate(context, R.layout.lobby, null);
        this.context       = context;

        this.playerListLay = (LinearLayout) LobbyLayout.findViewById(R.id.playerListLayout);
        this.playerNum     = (TextView)     LobbyLayout.findViewById(R.id.playerNum);
        this.playerNick    = (TextView)     LobbyLayout.findViewById(R.id.nickname);
        this.smallIcon     = (ImageView)    LobbyLayout.findViewById(R.id.smallIcon);

        this.paramsCard    = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        this.paramsCard.setMargins(40,70,40,70);

        this.playersOnGame = new ArrayList<>();
        this.playerCards   = new ArrayList<>();
    }

    public LinearLayout getLayout() {
        return this.LobbyLayout;
    }

    public void newPlayer(final Integer user_id, final String name) {

        final LinearLayout newCard = (LinearLayout) LinearLayout.inflate(context, R.layout.player_card, null);
        newCard.setLayoutParams(paramsCard);
        // Set name on player card
        TextView nameView = (TextView) newCard.findViewById(R.id.playName);
        nameView.setText(name);
        // Set robot avatar
        ImageView avatarView = (ImageView) newCard.findViewById(R.id.playAvatar);

        int tokenID = user_id;
        while(tokenID >= tokenImages.length) tokenID -= tokenImages.length;

        avatarView.setImageResource(tokenImages[tokenID]);

        newCard.setTag(user_id);
        playerCards.add(newCard);

        // Add new player to list
        Player p = new Player(0,"");
        p.setUserId(user_id);
        playersOnGame.add(p);

        // Add card to lobby interface
        playerListLay.addView(newCard);
        // Update number of players
        playerNum.setText(String.valueOf(playersOnGame.size()));

    }

    public void setPlayerData(Integer user_id, final String nickname) {
        current_id   = user_id;
        current_nick = nickname;

        playerNick.setText(nickname);

        int tokenID = user_id;
        while(tokenID >= tokenImages.length) tokenID -= tokenImages.length;
        this.smallIcon.setImageResource(tokenImages[tokenID]);
    }

    public void delPlayer(final Integer user_id) {
        // Check if the current client is deleted
        if (Objects.equals(user_id, current_id)) {
            playersOnGame.clear();

        }
        else {
            // Search and remove a player from list
            for ( Player p : playersOnGame) {
                if (p.getUserId() == user_id) {
                    playersOnGame.remove(p);
                    break;
                }
            }
        }

        // Check if the current client is deleted
        if (Objects.equals(user_id, current_id)) {
            playerListLay.removeAllViews();
            playerCards.clear();
            Log.v("Del", String.valueOf(user_id));
        }
        else {
            // Search and remove a player from list
            for (LinearLayout lay : playerCards) {
                if (lay.getTag() == user_id) {
                    playerListLay.removeView(lay);
                    playerCards.remove(lay);
                    break;
                }
            }
        }

        // Update number of players
        playerNum.setText(String.valueOf(playersOnGame.size()));
    }
}
