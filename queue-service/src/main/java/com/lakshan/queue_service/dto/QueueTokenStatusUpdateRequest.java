package com.lakshan.queue_service.dto;

import com.lakshan.queue_service.entity.QueueTokenStatus;

public class QueueTokenStatusUpdateRequest {

    private QueueTokenStatus status;

    public QueueTokenStatus getStatus() { return status; }
    public void setStatus(QueueTokenStatus status) { this.status = status; }
}
