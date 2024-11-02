package com.example.practica19_multitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class CLienzo extends View {

    private final int SIZE = 80;
    private SparseArray<PointF> punterosActivos = new SparseArray<PointF>();
    private Paint myPaint = new Paint();
    private int[]colors = {
            Color.BLUE, Color.GREEN, Color.MAGENTA, Color.BLACK, Color.CYAN,
            Color.GRAY, Color.RED, Color.LTGRAY, Color.YELLOW};
    private Paint textPaint = new Paint();

    int time = 0;

    public CLienzo(Context context){
        super(context);
        myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40f);
        Thread hilo = new Thread(){
            public synchronized void run(){
                while(true){
                    try{
                        Thread.sleep(1000);
                        if (punterosActivos.size() != 0 && time < 11) {
                            time += 1;
                        }
                        // Checar que existan punteros
                        if(punterosActivos.size() == 0){
                            time = 0;
                        }else if (time > 10){  // Terminar temporizador y elegir un puntero
                            selectWinner();
                        }
                        invalidate();
                    }catch(InterruptedException e){

                    }
                }
            }
        };
        hilo.start();
    }

    public void selectWinner(){
        // Genera un índice aleatorio
        Random random = new Random();
        int randomIndex = random.nextInt(punterosActivos.size());

        // Obtiene el ID y el puntero ganador
        int ganadorId = punterosActivos.keyAt(randomIndex);
        PointF punteroGanador = punterosActivos.valueAt(randomIndex);

        // Limpia el array y guarda solo el puntero ganador
        punterosActivos.clear();
        punterosActivos.put(ganadorId, punteroGanador);

        // Ahora `punterosActivos` solo contiene el puntero ganador
        invalidate(); // Redibuja para reflejar el cambio
    }

    public boolean onTouchEvent(MotionEvent event){
        int indice = event.getActionIndex();
        // abtenemos el puntero
        int apuntadorId = event.getPointerId(indice);
        // Obtiene el puntero relacionado
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN ||
        event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
            // Agregamos el puntero a la lista
            PointF f = new PointF();
            f.x = event.getX(indice);
            f.y = event.getY(indice);
            punterosActivos.put(apuntadorId, f);
        }
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            // Puntero se movió
            int size = event.getPointerCount();
            int i = 0;
            while (i < size) {
                int pointerId = event.getPointerId(i);
                PointF puntero = punterosActivos.get(event.getPointerId(i));
                if(puntero != null){
                    puntero.x = event.getX(i);
                    puntero.y = event.getY(i);
                }
                i++;
            }
        }
        if(event.getActionMasked() == MotionEvent.ACTION_UP ||
                    event.getActionMasked() == MotionEvent.ACTION_POINTER_UP ||
                    event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            punterosActivos.remove(apuntadorId);
        }
        invalidate();
        return true;
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        // pintamos todos los punteros
        int size = punterosActivos.size();
        int i = 0;
        PointF puntero = new PointF();
        while(i < size){
            puntero = punterosActivos.valueAt(i);
            if(puntero != null){
                myPaint.setColor(colors[i % 9]);
            }
            canvas.drawCircle(puntero.x, puntero.y, SIZE, myPaint);
            i++;
        }
        canvas.drawText("Total punteros: " + punterosActivos.size(), 10f, 80f, textPaint);
        canvas.drawText("Tiempo: " + time, 10f, 120f, textPaint);
        super.onDraw(canvas);
    }
}
