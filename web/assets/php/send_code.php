<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\SMTP;

require 'PHPMailer/src/Exception.php';
require 'PHPMailer/src/PHPMailer.php';
require 'PHPMailer/src/SMTP.php';

//Create an instance; passing `true` enables exceptions
$mail = new PHPMailer(true);
function sendCode($email, $subject, $code)
{
    global $mail;
    try {
        //Server settings
        $mail->SMTPDebug = SMTP::DEBUG_SERVER;                      //Enable verbose debug output
        $mail->isSMTP();                                            //Send using SMTP
        $mail->Host = 'smtp.gmail.com';                     //Set the SMTP server to send through
        $mail->SMTPAuth = true;                                   //Enable SMTP authentication
        $mail->Username = 't32337817@gmail.com';                     //SMTP username
        $mail->Password = 'pbmbbsbykwcokuja';                               //SMTP password
        $mail->SMTPSecure = 'tls';            //Enable implicit TLS encryption
        $mail->Port = 587;                                    //TCP port to connect to; use 587 if you have set `SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS`

        //Recipients
        $mail->setFrom('verify@CodeKendra.com', 'Verify CodeKendra');
        $mail->addReplyTo('support@CodeKendra.com', 'CodeKendra Support');
        $mail->addAddress($email);


        //Attachments
        // $mail->addAttachment('../img/email_sending.png');


        //Content
        $mail->isHTML(true);                                  //Set email format to HTML
        $mail->Subject = $subject;
        $mail->Body = '<strong>Dear User,</strong> <br>
    Your Verification code is : <br>
    <strong>' . $code . '</strong>';
        // $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';


        $mail->send();
        echo 'Message has been sent';
    } catch (Exception $e) {
        echo "Message could not be sent. Mailer Error: {$mail->ErrorInfo}";
    }
}
// sendCode('hwaukff@gmail.com', 'CodeKendra Verification Code', '123456');
