package com.arunaenterprisesbackend.ArunaEnterprises.Entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReplyStatusRequest {
    private boolean replyStatus;

    public boolean isReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(boolean replyStatus) {
        this.replyStatus = replyStatus;
    }
}
