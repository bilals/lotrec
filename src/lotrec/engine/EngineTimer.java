/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.engine;

/**
 *
 * @author said
 */
public class EngineTimer {
    
    private long elapsedTime = 0;
    private long lastStartTime = 0;
    private boolean paused;
    
    public void start(){              
        lastStartTime = System.currentTimeMillis();
        paused = false;
    }    
    
    public void pause(){
        elapsedTime += System.currentTimeMillis() - lastStartTime;
        paused = true;
    }       

    public void resume(){
        lastStartTime = System.currentTimeMillis();
        paused = false;
    }       
    
    public void stop(){
        if(!paused){
           elapsedTime += System.currentTimeMillis() - lastStartTime; 
        }
        paused = false;
    }       

    public long getElapsedTime(){
        return elapsedTime;
    }

    public long getCurrentElapsedTime(){
        return elapsedTime +(System.currentTimeMillis() - lastStartTime);
    }
}
