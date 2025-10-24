package model.DataClasses;

import java.util.ArrayList;

public class Result<TypeContent> {
    private ResultStatus Status;
    private TypeContent Result;
    private ArrayList<String> Comment;
    public Result(ResultStatus status, TypeContent result){
        this.Status = status;
        this.Result = result;
        this.Comment = new ArrayList<String>();
    }
    public Result(ResultStatus status, TypeContent result, ArrayList<String> comment){
        this.Status = status;
        this.Result = result;
        this.Comment = comment;
    }

    public TypeContent getResult() {
        return Result;
    }

    public ResultStatus getStatus() {
        return Status;
    }

    public ArrayList<String> getComment() {
        return Comment;
    }
}
