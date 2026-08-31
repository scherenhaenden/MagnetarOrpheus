#!/bin/bash
set -euo pipefail

ICON_SOURCE="design/icon.png"
RES_DIR="app/src/main/res"

if [ ! -r "$ICON_SOURCE" ]; then
    echo "Error: source icon is not readable: $ICON_SOURCE" >&2
    exit 1
fi

if ! command -v magick >/dev/null 2>&1; then
    echo "Error: ImageMagick 7+ ('magick') is required." >&2
    exit 1
fi

if [ ! -d "$RES_DIR" ] || [ ! -w "$RES_DIR" ]; then
    echo "Error: resource directory is not writable: $RES_DIR" >&2
    exit 1
fi

# Function to generate icons
generate_icons() {
    local size=$1
    local density=$2
    local filename=$3
    local target_dir="$RES_DIR/mipmap-$density"
    
    mkdir -p "$target_dir"
    local output_file="$target_dir/$filename.webp"
    echo "Generating ${size}x${size} ${filename} for ${density}..."
    magick "$ICON_SOURCE" -resize "${size}x${size}" "$output_file"
    if [ ! -f "$output_file" ]; then
        echo "Error: icon was not generated: $output_file" >&2
        exit 1
    fi
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
