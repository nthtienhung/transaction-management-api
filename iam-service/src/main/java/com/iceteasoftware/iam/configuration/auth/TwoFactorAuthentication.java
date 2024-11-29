//package com.example.iamservice.configuration.auth;
//
//import com.example.iamservice.totp.DefaultCodeGenerator;
//import com.example.iamservice.constant.Constants;
//import com.example.iamservice.enums.MessageCode;
//import com.example.iamservice.exception.handle.InternalServerErrorException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TwoFactorAuthentication {
//    /**
//     * The function GenerateNewSecret a new secret for Two-Factor Authentication (2FA) to User.
//     *
//     * @return The generated secret.
//     */
//    public String generateNewSecret() {
//        return new DefaultSecretGenerator().generate();
//    }
//
//    /**
//     * Generates a QR code image URI based on the provided secret.
//     *
//     * @param secret The secret used to generate the QR code.
//     * @return The TOTP of the generated QR code image.
//     * @throws InternalServerErrorException if there's an error generating the QR code.
//     */
//    public String generateQrCodeImageUri(String secret) {
//        QrData data = new QrData.Builder()
//                .label(Constants.DEFAULT_AUTHENTICATION_2FA_LABEL)
//                .secret(secret)
//                .issuer(Constants.DEFAULT_AUTHENTICATION_2FA_ISSUER)
//                .algorithm(HashingAlgorithm.SHA256)
//                .digits(6)
//                .period(30)
//                .build();
//
//        QrGenerator generator = new ZxingPngQrGenerator();
//        byte[] imageData = new byte[0];
//        try {
//            imageData = generator.generate(data);
//        } catch (QrGenerationException e) {
//            e.printStackTrace();
//            throw new InternalServerErrorException(MessageCode.MSG1017);
//        }
//        return Utils.getDataUriForImage(imageData, generator.getImageMimeType());
//    }
//
//    /**
//     * Checks if the provided one-time password (OTP) is valid.
//     *
//     * @param secret The secret associated with the user.
//     * @param code   TOTP authentication.
//     * @return True TOTP is valid, false TOTP is invalid.
//     */
//    public boolean isTotpValid(String secret, String code) {
//        TimeProvider timeProvider = new SystemTimeProvider();
//        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA256);
//        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
//        verifier.setAllowedTimePeriodDiscrepancy(0);
//        return verifier.isValidCode(secret, code);
//    }
//}
