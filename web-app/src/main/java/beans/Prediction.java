package beans;

public class Prediction {
    private int historyId;
    private int userId;
    private String inputData;
    private String predictionRes;
    private String date;

    public Prediction() {}
    public int getHistoryId() {
        return historyId;
    }
    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getInputData() {
        return inputData;
    }
    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
    public String getPredictionRes() {
        return predictionRes;
    }
    public void setPredictionRes(String predictionRes) {
        this.predictionRes = predictionRes;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}