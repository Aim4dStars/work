package com.bt.nextgen.api.draftaccount.model;

/**
 * Created by L070354 on 24/08/2015.
 */
public class TransitionStateDto {

    private String state;
    private String date;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TransitionStateDto(String state, String date){
        this.state = state;
        this.date = date;
    }

    public boolean equals(Object object) {
        if (object instanceof TransitionStateDto)
        {
            TransitionStateDto that = (TransitionStateDto)object;
            if(this.getDate().equals(that.getDate()) && this.getState().equals(that.getState())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 5;
        int result = 1;
        result = prime * result
                + ((state == null) ? 0 : state.hashCode());
        result = prime * result
                + ((date == null) ? 0 : date.hashCode());
        return result;
    }

}
