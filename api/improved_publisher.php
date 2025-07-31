<?php
class PieSocketPublisher {
    public static function publishNewPost($data) {
        // For now, just log it - no actual broadcasting
        error_log("Would broadcast new post: " . json_encode($data));
        return true;
    }
}
?>