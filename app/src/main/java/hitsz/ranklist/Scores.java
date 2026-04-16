package hitsz.ranklist;

/**
 * 分数实体类
 */
public class Scores implements Comparable<Scores> {
    private int score;
    private String playerID;

    public Scores(int score, String playerID) {
        this.score = score;
        this.playerID = playerID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    @Override
    public int compareTo(Scores o) {
        return o.score - this.score; // 降序排列
    }
}
