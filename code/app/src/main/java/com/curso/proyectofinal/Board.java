package com.curso.proyectofinal;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.util.TypedValue.applyDimension;
import static com.curso.proyectofinal.Constant.ACTION_PUSH;
import static com.curso.proyectofinal.R.drawable.blackheart;
import static com.curso.proyectofinal.R.drawable.bu;
import static com.curso.proyectofinal.R.drawable.fw1;
import static com.curso.proyectofinal.R.drawable.fw2;
import static com.curso.proyectofinal.R.drawable.fw3;
import static com.curso.proyectofinal.R.drawable.heart;
import static com.curso.proyectofinal.R.drawable.tl;
import static com.curso.proyectofinal.R.drawable.tr;
import static com.curso.proyectofinal.R.drawable.ut;
/**
 * Created by jsoto on 01-07-17.
 */

public class Board {

    private final RelativeLayout BoardLayout;
    private final Context context;
    private final Button btnHideLayout;
    private final Button infoLabel;
    private final LinearLayout cardsScroll;
    private final LinearLayout cardsScrollLayout;
    private final HorizontalScrollView frameHScroll;

    private final ImageView popUpImage;
    private final TextView popUpTitle;
    private final TextView popUpContentText;
    private final ImageView popUpContentImage;
    private List<Integer> cardResources;

    private LinearLayout   popupCardLayout;

    private final TextView popupCardName;
    private ImageView      popupCardImage;

    private LinearLayout   tablero;                         // Instancia LinearLayout de las filas del tablero
    private FrameLayout    frameTablero;                    // Instancia FrameLayout donde se dibuja el tablero
    private RelativeLayout frameRelScroll;                  // Instancia FrameLayout donde se dibuja el tablero
    public ImageView[][] cuadros_tablero;                   // Matriz en donde se instancia cada ImageView dentro del tablero

    private int tablero_size,tablero_ancho;                 // Tamaño (cantidad de cuadros) del tablero y ancho de cada cuadro (cuadrado)
    private int boardSize;
    private int guiWidth;
    private boolean guiIsVisible;
    private int myID;

    private boolean enableDrag;                      // habilita drag de las cartas para ordenamiento

    // Variables de lógica Logic
    private int playerNumber;                        // Entero con el numero de fichas/jugadores
    int informacion[][];                             // Matriz con la información de cada celda del tablero (solo para carga inicial en crear matriz)

    private String myUserName;
    Player currentPlayerNDrawing;

    FrameLayout[]  layoutCardPositions;
    Player[]       Players;

    final public int[] tokenImages = {R.drawable.tkbender, R.drawable.tkr2d2, R.drawable.tkwalle, R.drawable.tkmegaman};
    private boardCallback callback = null;

    public Board(Context context, int width, Player[] players) {

        this.BoardLayout            = (RelativeLayout) RelativeLayout.inflate(context, R.layout.tablero, null);
        this.context                = context;

        this.layoutCardPositions    = new FrameLayout[9];
        this.cardResources          = new ArrayList<>();
        this.Players                = players;

        this.cardsScroll            = (LinearLayout) this.BoardLayout.findViewById( R.id.cardsScroll );
        this.cardsScrollLayout      = (LinearLayout) this.BoardLayout.findViewById( R.id.cardsScrollLayout );
        this.popupCardLayout        = (LinearLayout) this.BoardLayout.findViewById(R.id.popupCardLayout);

        this.popupCardName          = (TextView)     this.BoardLayout.findViewById(R.id.popUpCardName);
        this.frameTablero           = (FrameLayout)  this.BoardLayout.findViewById( R.id.frameTablero );    // Frame donde se dibujará el tablero
        this.frameRelScroll         = (RelativeLayout)  this.BoardLayout.findViewById( R.id.frameRelScroll );
        this.btnHideLayout          = (Button)       this.BoardLayout.findViewById(R.id.btnHideLayout);
        this.infoLabel              = (Button)       this.BoardLayout.findViewById(R.id.infoLabel);

        this.popUpContentImage      = (ImageView)    this.BoardLayout.findViewById(R.id.popUpContentImage);
        this.popUpTitle             = (TextView)     this.BoardLayout.findViewById(R.id.popUpTitle);
        this.popUpImage             = (ImageView)    this.BoardLayout.findViewById(R.id.popUpImage);
        this.popUpContentText       = (TextView)     this.BoardLayout.findViewById(R.id.popUpContentText);

        this.frameHScroll            = (HorizontalScrollView)   this.BoardLayout.findViewById(R.id.frameHScroll);

        this.layoutCardPositions    = new FrameLayout[9];

        this.tablero                = new LinearLayout(context);                 // Se inicia LinearLayout de filas del tablero
        this.tablero.setOrientation(LinearLayout.VERTICAL);                      // Se setea LinearLayout de filas como vertical
        this.frameTablero.addView(tablero);                                      // Se agrega la instancia LinearLayout filas a instancia frameTablero
        this.popupCardLayout.setVisibility(View.INVISIBLE);

        for (int i = 0; i < 9; ++i) {
            String resName = String.format(Locale.US, "cardPosition%d", i + 1);
            int resID = context.getResources().getIdentifier(resName, "id", context.getPackageName());
            this.layoutCardPositions[i]  = (FrameLayout) this.BoardLayout.findViewById(resID);

            this.cardResources.add(i, resID);
        }

        // Valores iniciales lógica
        this.boardSize              = 12;
        this.tablero_size           = 0;                                        // Se inicial el tamaño del tablero en 0 (para que no sea null)
        this.tablero_ancho          = width / 10;                               // Se setea el ancho inicial por defecto como 150
        this.guiWidth               = width;
        this.guiIsVisible           = false;

        //  Hide cards on start
        this.setVisible(false);
    }

    public void registerCallback(boardCallback callback) {
        this.callback = callback;
    }

    public void showNotify(String title, String subTitle, int imgResource, int imgContent, String textContent) {

        popUpTitle.setText(title);

        if (imgResource != -1) {
            popUpImage.setImageResource(imgResource);
            popUpImage.setVisibility(View.VISIBLE);
        }
        else {
            popUpImage.setVisibility(View.GONE);
        }

        if (imgContent != -1) {
            popUpContentImage.setImageResource(imgContent);
            popUpContentImage.setVisibility(View.VISIBLE);
        }
        else {
            popUpContentImage.setVisibility(View.GONE);
        }

        if (textContent != null) {
            popUpContentText.setText(textContent);
            popUpContentText.setVisibility(View.VISIBLE);
        }
        else {
            popUpContentText.setVisibility(View.GONE);
        }

        if (subTitle != null) {
            popupCardName.setText(subTitle);
            popupCardName.setVisibility(View.VISIBLE);
        }
        else {
            popupCardName.setVisibility(View.GONE);
        }

        popupCardLayout.setVisibility(View.VISIBLE);
        new cardAnimation(1, true, 7).run();
    }

    public int[][] getInformationMat() {
        return this.informacion;
    }

    public void setPlayerData(Integer user_id, final String nickname) {
        this.myID = user_id;
        this.myUserName = nickname;
    }

    public RelativeLayout getLayout() {
        return this.BoardLayout;
    }

    public void zoomAction(float zoom) {
        this.guiZoomAction((int)zoom, this.guiWidth /this.boardSize,this.guiWidth/10);
    }

    public boolean isVisible() {
        return this.guiIsVisible;
    }

    public void setVisible(boolean visible) {
        this.guiIsVisible = visible;

        if (visible) {
            cardsScroll.animate().translationY(0);
            btnHideLayout.animate().translationY(0);
            infoLabel.animate().translationY(0);
        }
        else {
            cardsScroll.animate().translationY(applyDimension( TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics() ));
            btnHideLayout.animate().translationY(applyDimension( TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics() ));
            infoLabel.animate().translationY(applyDimension( TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics() ));;
        }
    }

    public void increasePlayerNum() {
        this.playerNumber = this.playerNumber + 1;
    }

    public void deacreasePlayerNum() {
        this.playerNumber = this.playerNumber - 1;
    }

    public void guiLoadMatrix(int columnas, int filas){
        cuadros_tablero = new ImageView[filas][columnas];
        for(int i = 0; i<filas; i++){
            for(int j=0; j<columnas; j++){
                ImageView cuadro = new ImageView(context);
                cuadro.setBackgroundColor(0xFF00FF00);

                String resName;
                if (informacion[i][j] <= 16) {
                    resName = String.format(Locale.US, "chrt_%d", informacion[i][j]);
                }
                else {
                    resName = "chrt_0";
                }
                int resID = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());

                cuadro.setImageResource(resID);
                cuadro.setScaleType(ImageView.ScaleType.CENTER);
                cuadro.setAdjustViewBounds(true);
                cuadros_tablero[i][j] = cuadro;
            }
        }
    }

    private int[][] openMap(int map_id) {
        InputStream in = context.getResources().openRawResource(map_id);
        BufferedReader r = new BufferedReader(new InputStreamReader(in));

        int[][] mapa = new int[boardSize][boardSize];
        int i = 0;
        try {
            String line;
            while ((line = r.readLine()) != null) {
                int j = 0;

                for (String col: line.split(" ")) {
                    if (!col.isEmpty()) {
                        Log.v("MAP", "i: " + i + " - j: " + j + " - "  + col);
                        mapa[i][j] = Integer.parseInt(col);
                        j++;
                    }
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapa;
    }


    public void guiDrawMatriz(LinearLayout linearLayout, int columnas, int filas, int ancho){
        if(( linearLayout).getChildCount() > 0)
            (linearLayout).removeAllViews();

        linearLayout.setBackgroundColor(0xFF000000);

        for(int i = 0; i<filas; i++){
            LinearLayout layout_anidado = new LinearLayout(context);
            linearLayout.addView(layout_anidado, i);
            for(int j=0; j<columnas; j++){
                try{
                    ((ViewGroup)cuadros_tablero[i][j].getParent()).removeAllViews();
                }
                catch (Exception e){
                    Log.v("tag","tag");
                }
                layout_anidado.addView(cuadros_tablero[i][j],j);
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) cuadros_tablero[i][j].getLayoutParams();
                params1.width  = ancho;
                params1.height = ancho;
                params1.setMargins(1,1,1,1);
            }
        }
    }

    public void guiSetActiveCards(boolean state, Player playerN){

        if ( state ){ // activar movimiento de cartas mediante drag & drop
            this.enableDrag = true;
            // show cards
            this.setVisible(true);
        }
        else{ // desactivar movimiento de cartas mediante drag & drop
            this.enableDrag = false;
            guiFlipCards(playerN);
        }
    }

    public void guiFlipCards(Player playerN) {

        Hand pHand = playerN.getHand();

        for ( int i = 0; i < Constant.NCARDS_PER_HAND ; i++){
            Card carta = pHand.getCard(i);
            if (carta.getPosition() >= Constant.NCARDS_PER_HAND_PLAYABLE){
                carta.getImageView().setImageResource(R.drawable.back);
            }
        }
    }

    // Añade un pj al tablero, por ahora es de prueba y debe ser modificada para admitir "x" pj
    public void guiDrawToken(Player playerN) {
        Ptoken pToken = playerN.getToken();
        pToken.setTokenImage(new ImageView(context));

        ImageView pTokemImage = pToken.getTokenImage();

        int tokenID = playerN.getUserId();
        while(tokenID >= tokenImages.length) tokenID -= tokenImages.length;
        pTokemImage.setImageResource(tokenImages[tokenID]);

        frameTablero.addView( pTokemImage );
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pTokemImage.getLayoutParams();
        params.width = tablero_ancho;
        params.height = tablero_ancho;

        int xposition =  pToken.getCurrentPosition().getX();
        int yposition =  pToken.getCurrentPosition().getY();

        pTokemImage.setX( cuadros_tablero[yposition][xposition].getX() );
        pTokemImage.setY( ((LinearLayout) cuadros_tablero[yposition][xposition].getParent()).getY() );
    }


    public void guiZoomAction(final int step, final int min, final int max){

        if (tablero_ancho == intlimit(tablero_ancho + step, min, max)) {    // El ancho del tablero se redefine segun posicion anterior
            return;
        } else {
            tablero_ancho = intlimit(tablero_ancho + step, min, max);
            // Se dibuja el tablero
            guiDrawMatriz(tablero, boardSize,
                    boardSize, tablero_ancho);
            new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < Players.length; i++) {
                        if (Players[i] != null) {

                            Log.v("WV", "Ajustes pj " + i);
                            final Player playerN = Players[i];
                            final Ptoken pToken = playerN.getToken();
                            final ImageView pTokemImage = pToken.getTokenImage();

                            int xposition = pToken.getCurrentPosition().getX();
                            int yposition = pToken.getCurrentPosition().getY();
                            float rotation = pTokemImage.getRotation();
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                                    playerN.getToken().getTokenImage().getLayoutParams();    // Se extraen los parámetros del pj
                            params.width = tablero_ancho;              // Se define nuevamente el ancho del pj
                            params.height = tablero_ancho;              // Se define nuevamente el alto del pj

                            pTokemImage.setRotation(0);
                            pTokemImage.setX(0);
                            pTokemImage.setY(0);
                            pTokemImage.setPivotX(tablero_ancho / 2);
                            pTokemImage.setPivotY(tablero_ancho / 2);
                            pTokemImage.setRotation(rotation);
                            pTokemImage.setX((tablero_ancho + 2) * xposition);
                            pTokemImage.setY((tablero_ancho + 2) * yposition);
                            Log.v("WV", "Posicion nueva obtenidax = " + pTokemImage.getX() + " y = " + pTokemImage.getY());
                        }
                    }
                }
            }.run();
        }
    }

    public void guiLoadHand(Player playerN) {
        Hand cHand = playerN.getHand();
        if (callback != null) {
            callback.playCard(2);
        }

        for (int carta = 0; carta < Constant.NCARDS_PER_HAND; carta++) {
            cHand.getCard(carta).setImageView(new ImageView( context ));
            Card cCard = cHand.getCard(carta);

            switch ( cCard.getType()){
                case Constant.CARD_MOVE_1:
                    cCard.getImageView().setImageResource( fw1 );
                    break;
                case Constant.CARD_MOVE_2:
                    cCard.getImageView().setImageResource( fw2 );
                    break;
                case Constant.CARD_MOVE_3:
                    cCard.getImageView().setImageResource( fw3 );
                    break;
                case Constant.CARD_ROTATE_LEFT:
                    cCard.getImageView().setImageResource( tl );
                    break;
                case Constant.CARD_ROTATE_RIGHT:
                    cCard.getImageView().setImageResource( tr );
                    break;
                case Constant.CARD_ROTATE_180:
                    cCard.getImageView().setImageResource( ut );
                    break;
                case Constant.CARD_BACK_UP:
                    cCard.getImageView().setImageResource( bu );
                    break;
                default:
                    break;
            }

            layoutCardPositions[ carta ].removeAllViews();
            layoutCardPositions[ carta ].addView( cCard.getImageView() );
            TextView prioridadCarta = new TextView(context);
            prioridadCarta.setTextColor(Color.parseColor("#000000"));

            prioridadCarta.setText(String.valueOf(cCard.getPriority()));
            layoutCardPositions[ carta ].addView(prioridadCarta);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cCard.getImageView().getLayoutParams();
            params.width        = ( int ) applyDimension( TypedValue.COMPLEX_UNIT_DIP, 70, context.getResources().getDisplayMetrics() );
            params.height       = ( int ) applyDimension( TypedValue.COMPLEX_UNIT_DIP, 90, context.getResources().getDisplayMetrics() );
            params.bottomMargin = ( int ) applyDimension( TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics() );
            params.gravity      = Gravity.RIGHT;

        }
        for ( int carta = 0; carta < Constant.NCARDS_PER_HAND ; carta++){
            layoutCardPositions[carta].setOnDragListener(new myDragListener());
            playerN.getHand().getCard(carta).getImageView().setOnTouchListener(new MyTouchListener());
            attenuateCard(carta, false);
        }
    }

    public void guiShowTurnPopup (Player playerN, int cardType, int cardPriority ) {
        int rs = -1;
        if (callback != null) {
            callback.playCard(3);
        }
        switch ( cardType ) {
            case Constant.CARD_MOVE_1:
                rs = ( fw1 );
                break;
            case Constant.CARD_MOVE_2:
                rs = ( fw2 );
                break;
            case Constant.CARD_MOVE_3:
                rs = ( fw3 );
                break;
            case Constant.CARD_ROTATE_LEFT:
                rs = ( tl );
                break;
            case Constant.CARD_ROTATE_RIGHT:
                rs = ( tr );
                break;
            case Constant.CARD_ROTATE_180:
                rs = ( ut );
                break;
            case Constant.CARD_BACK_UP:
                rs = ( bu );
                break;
        }

        // Dibuja animacion
        int tokenID = playerN.getUserId();
        while(tokenID >= tokenImages.length) tokenID -= tokenImages.length;
        showNotify("Ahora juega", playerN.getUserName(), tokenImages[tokenID], rs, null);
    }

    public void guiDrawLife(Player playerN) {
        int lifeCounter;
        String resourceId;

        for ( lifeCounter = 0; lifeCounter < playerN.getLife(); lifeCounter++ ){
            resourceId = "heartContainers_" + lifeCounter;
            int resID  = context.getResources().getIdentifier(resourceId, "id", context.getPackageName());
            ((ImageView) this.BoardLayout.findViewById(resID)).setImageResource(heart);
        }
        for (; lifeCounter < Constant.PLAYER_INITIAL_LIFE; lifeCounter++){
            resourceId = "heartContainers_" + lifeCounter;
            int resID  = context.getResources().getIdentifier(resourceId, "id", context.getPackageName());
            ((ImageView) this.BoardLayout.findViewById(resID)).setImageResource(blackheart);
        }
    }

    public void guiDrawMovement (final Player player){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                guiDelayDrawMovement(player);
            }
        };
        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, 4000);
    }

    public void guiDelayDrawMovement( Player playerN ){
        int xposition;
        int yposition;
        int xActionPosition;
        int yActionPosition;

        Ptoken pToken = playerN.getToken();
        ImageView pTokemImage = pToken.getTokenImage();

        switch ( pToken.getAction().getType() ){
            case Constant.ACTION_NONE:
                Log.v("CMD","NONE");
                if (callback != null) {
                    callback.playRobot(4);
                }
                break;
            case Constant.ACTION_MOVE:
                Log.d("WV","MOVE");

                //Call musicplayer.playRobotEffect(0);
                if (callback != null) {
                    callback.playRobot(0);
                }
                xposition = pToken.getCurrentPosition().getX();
                yposition = pToken.getCurrentPosition().getY();
                pTokemImage.animate().translationX(cuadros_tablero[yposition][xposition].getX());
                pTokemImage.animate().translationY(((LinearLayout) cuadros_tablero[yposition][xposition].getParent()).getY() );
                break;
            case Constant.ACTION_ROTATE:
                Log.v("CMD","ROTate");
                //Call musicplayer.playRobotEffect(1);
                if (callback != null) {
                    callback.playRobot(1);
                }
                pTokemImage.setPivotX(pTokemImage.getWidth() / 2);
                pTokemImage.setPivotY(pTokemImage.getHeight() / 2);

                switch ( pToken.getOrientation() ){
                    case Constant.TOKEN_ORIENTATION_U:
                        pTokemImage.animate().rotation(180);
                        break;
                    case Constant.TOKEN_ORIENTATION_D:
                        pTokemImage.animate().rotation(0);
                        break;
                    case Constant.TOKEN_ORIENTATION_L:
                        pTokemImage.animate().rotation(90);
                        break;
                    case Constant.TOKEN_ORIENTATION_R:
                        pTokemImage.animate().rotation(270);
                        break;
                }
                break;
            case Constant.ACTION_FALL:
                Log.v("CMD","FALL");
                //Call musicplayer.playRobotEffect(2);
                if (callback != null) {
                    callback.playRobot(2);
                }
                xActionPosition = pToken.getAction().getPosition().getX();
                yActionPosition = pToken.getAction().getPosition().getY();
                currentPlayerNDrawing = playerN;
                pTokemImage.animate().translationX(cuadros_tablero[yActionPosition][xActionPosition].getX());
                pTokemImage.animate().translationY(((LinearLayout) cuadros_tablero[yActionPosition][xActionPosition].getParent()).getY() ).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        Ptoken pToken = currentPlayerNDrawing.getToken();
                        ImageView pTokemImage = pToken.getTokenImage();

                        pTokemImage.animate().scaleY((float)0.1).scaleX((float)0.1).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Ptoken pToken = currentPlayerNDrawing.getToken();
                                ImageView pTokemImage = pToken.getTokenImage();

                                int xposition = pToken.getCurrentPosition().getX();
                                int yposition = pToken.getCurrentPosition().getY();
                                pTokemImage.animate().translationX(cuadros_tablero[yposition][xposition].getX());
                                pTokemImage.animate().translationY(((LinearLayout) cuadros_tablero[yposition][xposition].getParent()).getY() );
                                pTokemImage.animate().scaleY((float)1).scaleX((float)1);
                            }
                        });
                    }
                });
                break;
            case ACTION_PUSH:
                Log.d("WV","PUSH");
                //Call musicplayer.playRobotEffect(3);
                if (callback != null) {
                    callback.playRobot(3);
                }
                xposition = pToken.getCurrentPosition().getX();
                yposition = pToken.getCurrentPosition().getY();
                pTokemImage.animate().translationX(cuadros_tablero[yposition][xposition].getX());
                pTokemImage.animate().translationY(((LinearLayout) cuadros_tablero[yposition][xposition].getParent()).getY() );
                break;
            default:
                Log.v("CMD","DEFAULT");
                break;
        }
    }

    public void guiGenerateLayout () {
        informacion = openMap(R.raw.map_1);
        guiLoadMatrix(this.boardSize,this.boardSize);
        guiDrawMatriz(this.tablero,this.boardSize,this.boardSize,this.tablero_ancho);
    }


    public void guiDrawAllPlayers() {
        for ( int i = 0; i < Players.length; i++) {
            if ( Players[i] != null ) {
                guiDrawToken(Players[i]);
            }
        }
    }

    public void attenuateCard(int cardIndex, boolean attenuate) {
        for (int i = 0; i < Constant.NCARDS_PER_HAND; i++) {
            if (Players[myID].getHand().getCard(i).getPosition() == cardIndex) {
                if (attenuate)
                    Players[myID].getHand().getCard(i).getImageView().setColorFilter(Color.argb(0x60, 0, 0, 0));
                else
                    Players[myID].getHand().getCard(i).getImageView().setColorFilter(Color.argb(0, 0, 0, 0));
                break;
            }
        }
    }

    /***********************************************************/
    /*******************  REDEFINICIONES  **********************/
    /***********************************************************/
    /*
    La definición propa de OnTouchListener permite generar el efecto "fantasma" de cuando se
    arrastran las imágenes (imageView)
     */
    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (!enableDrag)
                return false; // des-habilitar movimiento cartas en función del flag

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(null, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private class myDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            View view = (View)event.getLocalState();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.v("tag", "elemento tomado");
                    view.setVisibility(View.INVISIBLE);
                    if (callback != null) {
                        callback.playCard(0);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    Log.v("tag", "elemento soltado");
                    ViewGroup owner = (ViewGroup) view.getParent();
                    FrameLayout destiny = (FrameLayout) v;
                    int ownerPosition = cardResources.indexOf(owner.getId());
                    int destinyPosition = cardResources.indexOf(destiny.getId());

                    // Se verifica si la posición de destino corresponde a uno de los cuadros
                    if (ownerPosition >= 0 && destinyPosition >= 0) {
                        if (ownerPosition != destinyPosition) {
                            cardPositionChanger(owner, ownerPosition, destiny, destinyPosition, Players[myID], view);
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (callback != null) {
                        callback.playCard(1);
                    }
                    view.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

            return true;
        }

        void cardPositionChanger(ViewGroup owner, int ownerPosition, FrameLayout destiny, int destinyPosition, Player playerN, View view){
            // Borras los views desde los padres
            destiny.removeAllViews();
            owner.removeAllViews();
            // Actualiza posiciones de las cartas
            Hand pHand = playerN.getHand();

            int ownerCardPosition    = pHand.getCard(ownerPosition).getPosition();
            int destinyCardPosition  = pHand.getCard(destinyPosition).getPosition();

            pHand.getCard(ownerPosition).setPosition(destinyCardPosition);
            pHand.getCard(destinyPosition).setPosition(ownerCardPosition);

            // Inserta las views en los nuevos padres
            destiny.addView(pHand.getCard(ownerCardPosition).getImageView());
            owner.addView(pHand.getCard(destinyCardPosition).getImageView());

            // Agrega las prioridades
            TextView prioridadOwner   = new TextView(context);
            TextView prioridadDestiny = new TextView(context);

            prioridadOwner.setTextColor(Color.parseColor("#000000"));
            prioridadDestiny.setTextColor(Color.parseColor("#000000"));

            prioridadDestiny.setText(String.valueOf(pHand.getCard(destinyCardPosition).getPriority()));
            prioridadOwner.setText(String.valueOf(pHand.getCard(ownerCardPosition).getPriority()));

            prioridadOwner.setTextColor(Color.parseColor("#000000"));
            prioridadDestiny.setTextColor(Color.parseColor("#000000"));

            destiny.addView(prioridadOwner);
            owner.addView(prioridadDestiny);

            // Setea visible
            view.setVisibility(View.VISIBLE);

            String myString = "Orden de cartas: ";
            for (int i = 0; i < Constant.NCARDS_PER_HAND; i++) {
                myString = myString + pHand.getCard(i).getPosition() + ", ";
            }
        }
    }

    /**********************************************************/
    /*********************  CLASES  ***************************/
    /**********************************************************/
    /*
    * Clase (en forma de estructura) que almacena la informacion del pj
    * Cada pj debe tener como tag esta clase, ya que así se puede saber su posición en la matriz
    * */

    /*Se debe añadir una clase para los cuadros de la matriz*/

    private class cardAnimation implements Runnable {
        private float scale;
        private boolean par;
        private int rept;
        private Runnable setVis;

        public cardAnimation(float scale, boolean par, int rep) {
            this.scale = scale;
            this.par   = par;
            this.rept  = rep;

            setVis = new Runnable() {
                @Override
                public void run() {
                    popupCardLayout.setVisibility(View.INVISIBLE);
                }
            };
        }
        @Override
        public void run() {
            this.rept = this.rept - 1;

            if (this.rept > 0) {
                cardAnimation tmp = new cardAnimation( (float)(par ? scale - 0.1 : scale + 0.1), !this.par, this.rept);
                popupCardLayout.animate().scaleX(scale).scaleY(scale).withEndAction(tmp);
            }
            else {
                popupCardLayout.animate().scaleX(scale).scaleY(scale).withEndAction(setVis);
            }
        }
    }

    /*
    * Función de utilidad para limitar una variable entre un mínimo y un máximo
    * */
    public int intlimit(int entrada, int minimo, int maximo){
        if( entrada < minimo )      // Si es menor al mínimo se ajusta al mínimo
            return minimo;
        else if( entrada > maximo ) // Si es mayor que el máximo se ajusta al máximo
            return maximo;
        else                        // En caso contrario se devuelve el mismo valor
            return entrada;
    }
}

// prototype of callback
interface boardCallback {
    void playRobot(int type);
    void playCard(int type);
}
