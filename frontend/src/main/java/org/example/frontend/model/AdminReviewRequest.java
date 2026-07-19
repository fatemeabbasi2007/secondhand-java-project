package org.example.frontend.model;

public class AdminReviewRequest {
    private boolean approved;
    private String rejectionReason;

    public AdminReviewRequest() {}

    public AdminReviewRequest(boolean approved, String rejectionReason) {
        this.approved = approved;
        this.rejectionReason = rejectionReason;
    }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}