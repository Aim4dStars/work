package com.bt.nextgen.api.global.model;

import com.bt.nextgen.core.api.model.BaseDto;

public class GlobalDetailsDto extends BaseDto {
    private int unreadCount;
    private int unreadClientCount;
    private int unreadMyCount;

    public GlobalDetailsDto() {
    }

    /**
     * Constructs an instance of GlobalDetailsDto
     *
     * @param unreadClientCount
     * @param unreadMyCount
     */
    public GlobalDetailsDto(int unreadClientCount, int unreadMyCount) {
        this.unreadClientCount = unreadClientCount;
        this.unreadMyCount = unreadMyCount;
        this.unreadCount = unreadClientCount + unreadMyCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getUnreadClientCount() {
        return unreadClientCount;
    }

    public void setUnreadClientCount(int unreadClientCount) {
        this.unreadClientCount = unreadClientCount;
    }

    public int getUnreadMyCount() {
        return unreadMyCount;
    }

    public void setUnreadMyCount(int unreadMyCount) {
        this.unreadMyCount = unreadMyCount;
    }
}
