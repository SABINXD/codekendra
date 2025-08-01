<?php
class PieSocketPublisher {
    private static $apiKey = "EiQOSBF2l4E6OManqyUZOslqgBz75U0vPNBKQiAN";
    private static $channelId = "19650"; // FIXED: Use the correct channel ID
    private static $clusterId = "s15004.nyc1";
    
    public static function publish($event, $data) {
        $endpoint = "https://" . self::$clusterId . ".piesocket.com/api/publish";
        $payload = [
            "channelId" => self::$channelId,
            "event" => $event,
            "data" => $data
        ];
        
        $headers = [
            "Content-Type: application/json",
            "Authorization: Bearer " . self::$apiKey
        ];
        
        $ch = curl_init($endpoint);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        
        error_log("Publishing to PieSocket... Event: $event, Data: " . json_encode($data));
        $response = curl_exec($ch);
        error_log("PieSocket Response: " . $response);
        curl_close($ch);
        
        return $response;
    }
}
?>