/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.engine;

/**
 *
 * @author said
 */
public enum EngineStatus {
        NORMAL("running.."), FINISHED("has finished."), 
        SHOULDPAUSE("should pause.."), PAUSED("paused.."),
        SHOULDRESUME("should resume.."), RESUMED("has resumed.."),
        SHOULDSTOP("should stop.."), STOPPED("stopped.."),
        STEPRESUMING("resuming to next step.."),
        STEPRUNNING("running step forward.."), 
        STEPFINISHED("has finished last step.");
        private String statusDesc;

        EngineStatus(String s) {
            statusDesc = s;
        }

        @Override
        public String toString() {
            return statusDesc;
        }
}
