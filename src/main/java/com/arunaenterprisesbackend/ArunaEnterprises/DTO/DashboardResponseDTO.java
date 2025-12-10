package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;

import java.util.List;

public class DashboardResponseDTO {
    private List<Reel> reels;
    private List<AttendanceResponseDTO> attendances;

    public DashboardResponseDTO() {}

    public DashboardResponseDTO(List<Reel> reels, List<AttendanceResponseDTO> attendances) {
        this.reels = reels;
        this.attendances = attendances;
    }

    public List<Reel> getReels() {
        return reels;
    }

    public void setReels(List<Reel> reels) {
        this.reels = reels;
    }

    public List<AttendanceResponseDTO> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceResponseDTO> attendances) {
        this.attendances = attendances;
    }
}