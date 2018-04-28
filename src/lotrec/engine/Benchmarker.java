package lotrec.engine;

/**
 *
 * @author Said
 */
public class Benchmarker {

    private int nbTentativesClassic;
    private int nbTentativesLoTREC;
//    private int nbPromisingTentativesLoTREC;

    public Benchmarker(){
        nbTentativesClassic = 0;
        nbTentativesLoTREC = 0;
    }

    public void increaseNbTentativesClassic(int nbTentativesClassic){
        this.nbTentativesClassic += nbTentativesClassic;
    }

    public void increaseNbTentativesLoTREC(int nbTentativesLoTREC){
        this.nbTentativesLoTREC += nbTentativesLoTREC;
    }

    /**
     * @return the nbTentativesClassic
     */
    public int getNbTentativesClassic() {
        return nbTentativesClassic;
    }

    /**
     * @return the nbTentativesLoTREC
     */
    public int getNbTentativesLoTREC() {
        return nbTentativesLoTREC;
    }

}
