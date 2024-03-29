package rak.pixellwp.cycling.wallpaperService

import android.graphics.Rect
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImageInfo
import rak.pixellwp.cycling.models.TimelineImage

internal fun CyclingWallpaperService.CyclingWallpaperEngine.loadInitialImage(): ImageInfo {
    Log.v(cyclingWallpaperLogTag, "Load initial image collection= $imageCollection, timeline= $timelineImage, drawer= ${drawRunner.id}")
    return when {
        currentImageType == ImageType.TIMELINE && timelineImage != "" -> imageLoader.getImageInfoForTimeline(timelineImage, getTime(), weather)
        currentImageType == ImageType.COLLECTION && imageCollection != "" -> imageLoader.getImageInfoForCollection(imageCollection, getTime(), weather)
        else -> defaultImage
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.downloadFirstTimeImage() {
    if (imageCollection == "" && timelineImage == "") {
        imageCollection = "Jungle Waterfall"
        changeCollection()
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.changeCollection() {
    if (imageCollection.isNotBlank()) {
        val image = imageLoader.getImageInfoForCollection(imageCollection, getTime(), weather)
        changeImage(image)
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.changeTimeline() {
    if (timelineImage.isNotBlank()) {
        val image = imageLoader.getImageInfoForTimeline(timelineImage, getTime(), weather)
        changeImage(image)
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.changeImage(image: ImageInfo) {
    if (image != currentImage) {
        Log.d(cyclingWallpaperLogTag, "Changing from ${currentImage.name} to ${image.name}.")
        val previousImage = currentImage
        try {
            if (imageLoader.imageIsReady(image)) {
                currentImage = image
                if (image.isTimeline) {
                    drawRunner.image = imageLoader.loadTimelineImage(image)
                    if (drawRunner.image is TimelineImage && overrideTimeline) {
                        (drawRunner.image as TimelineImage).setTimeOverride(overrideTime)
                    }
                } else {
                    drawRunner.image = imageLoader.loadImage(image)
                }
                determineMinScaleFactor()

            } else {
                imageLoader.downloadImage(image)
            }
        } catch (e: Exception){
            Log.e(cyclingWallpaperLogTag, "Unable to change image from ${previousImage.name} to ${currentImage.name}.")
        }
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.getOffsetImage(): Rect {
    if (parallax && !isPreview) {
        val totalPossibleOffset = drawRunner.image.getImageWidth() - imageSrc.width()
        val offsetPixels = totalPossibleOffset * screenOffset
        val left = offsetPixels.toInt()
        return Rect(left, imageSrc.top, left + imageSrc.width(), imageSrc.bottom)
    }
    return imageSrc
}