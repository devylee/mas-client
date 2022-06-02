package com.byteidolon.mas.client;

public class MasAssets {
    private String description = "";
    private String path = null;
    private boolean removable = false;
    private boolean primary = false;
    private boolean emulated = false;

    public String getDescription() {
        return description;
    }

    public MasAssets setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getPath() {
        return path;
    }

    public MasAssets setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isRemovable() {
        return removable;
    }

    public MasAssets setRemovable(boolean removable) {
        this.removable = removable;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public MasAssets setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public boolean isEmulated() {
        return emulated;
    }

    public MasAssets setEmulated(boolean emulated) {
        this.emulated = emulated;
        return this;
    }
}
