package hitsz.ranklist;

import java.util.List;

public interface ScoresDao {
    void findById(String playerID);
    List<Scores> getAllScores();
    void doAdd(Scores scores);
    void doDelete(String playerID);
    void sortPrintSave();
}
