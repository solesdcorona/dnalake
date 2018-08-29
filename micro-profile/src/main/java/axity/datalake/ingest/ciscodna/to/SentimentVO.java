package axity.datalake.ingest.ciscodna.to;

public class SentimentVO {

    private float score;

    private long negative;

    private long middle;

    private long positive;




    public long getPositive() {
        return positive;
    }

    public void setPositive(long positive) {
        this.positive = positive;
    }

    public long getMiddle() {
        return middle;
    }

    public void setMiddle(long middle) {
        this.middle = middle;
    }

    public long getNegative() {
        return negative;
    }

    public void setNegative(long negative) {
        this.negative = negative;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getImpact(){
        return "2-Significativo/Amplio";
    }


    public String getUrgencia(){
        if(middle>=2){
            return "4-Baja";
        }
        if(middle>=1){
            return "3-Media";
        }
        if(((-1.0f)-score)>-1.5f){
            return "1-Crítica";
        }
        return "2-Alta";
    }

}
