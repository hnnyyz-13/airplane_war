package hitsz.ranklist;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分数数据访问实现类
 */
public class ScoresDaoImpl {
    private int difficulty;
    private Context context;
    private List<Scores> scoresList;

    public ScoresDaoImpl(int difficulty, Context context) {
        this.difficulty = difficulty;
        this.context = context;
        this.scoresList = new ArrayList<>();
        loadScores();
    }

    /**
     * 从文件加载分数数据
     */
    private void loadScores() {
        scoresList.clear();
        // 创建 hitsz 目录
        File hitszDir = new File(context.getFilesDir(), "hitsz");
        if (!hitszDir.exists()) {
            hitszDir.mkdirs();
        }
        File file = new File(hitszDir, "ranklist_" + difficulty + ".txt");
        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        int score = Integer.parseInt(parts[0]);
                        String playerID = parts[1];
                        scoresList.add(new Scores(score, playerID));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            Collections.sort(scoresList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存分数数据到文件
     */
    private void saveScores() {
        // 创建 hitsz 目录
        File hitszDir = new File(context.getFilesDir(), "hitsz");
        if (!hitszDir.exists()) {
            hitszDir.mkdirs();
        }
        File file = new File(hitszDir, "ranklist_" + difficulty + ".txt");

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {

            for (Scores scores : scoresList) {
                osw.write(scores.getScore() + "," + scores.getPlayerID() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加新的分数
     */
    public void doAdd(Scores scores) {
        scoresList.add(scores);
    }

    /**
     * 删除指定的分数
     */
    public void deleteScore(String playerID, int score) {
        for (int i = 0; i < scoresList.size(); i++) {
            Scores s = scoresList.get(i);
            if (s.getPlayerID().equals(playerID) && s.getScore() == score) {
                scoresList.remove(i);
                break;
            }
        }
    }

    /**
     * 排序并保存分数
     */
    public void sortPrintSave() {
        Collections.sort(scoresList);
        saveScores();
    }

    /**
     * 获取所有分数
     */
    public List<Scores> getAllScores() {
        return scoresList;
    }
}