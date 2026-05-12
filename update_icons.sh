#!/bin/bash
ICON_SOURCE="design/icon.png"
RES_DIR="app/src/main/res"

# Function to generate icons
generate_icons() {
    local size=$1
    local density=$2
    local filename=$3
    local target_dir="$RES_DIR/mipmap-$density"
    
    mkdir -p "$target_dir"
    magick "$ICON_SOURCE" -resize "${size}x${size}" "$target_dir/$filename.webp"
}

# Launcher icons
generate_icons 48 mdpi ic_launcher
generate_icons 72 hdpi ic_launcher
generate_icons 96 xhdpi ic_launcher
generate_icons 144 xxhdpi ic_launcher
generate_icons 192 xxxhdpi ic_launcher

# Round launcher icons
generate_icons 48 mdpi ic_launcher_round
generate_icons 72 hdpi ic_launcher_round
generate_icons 96 xhdpi ic_launcher_round
generate_icons 144 xxhdpi ic_launcher_round
generate_icons 192 xxxhdpi ic_launcher_round

# Adaptive foreground icons
generate_icons 108 mdpi ic_launcher_foreground
generate_icons 162 hdpi ic_launcher_foreground
generate_icons 216 xhdpi ic_launcher_foreground
generate_icons 324 xxhdpi ic_launcher_foreground
generate_icons 432 xxxhdpi ic_launcher_foreground
