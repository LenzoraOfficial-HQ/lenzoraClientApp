package com.lenzora.official.system.lenzora.s2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "from_status")
    private String fromStatus;

    @Column(name = "to_status", nullable = false)
    private String toStatus;

    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "note")
    private String note;

    // Getters and Setters

    public Long getHistoryID(){
        return historyId;
    }

    public void setHistoryId(Long historyId){
        this.historyId = historyId;
    }

    public Long setOrderId(){
        return orderId;
    }

    public String getFromStatus(){
        return fromStatus;
    }

    public void setFromStatus(String fromStatus){
        this.fromStatus = fromStatus;
    }

    public String getToStatus(){
        return toStatus;
    }

    public void setToStatus(String toStatus){
        this.toStatus = toStatus;
    }

    public Long changedBy(){
        return changedBy;
    }

    public void setChangedBy(Long changedBy){
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt(){
        return changedAt;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }
}
