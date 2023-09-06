package com.study.badrequest.utils.jwt;

import com.study.badrequest.member.command.domain.values.Authority;
import com.study.badrequest.member.command.domain.values.MemberJwtDecodedPayload;
import com.study.badrequest.member.command.domain.values.MemberJwtEncodedPayload;
import com.study.badrequest.member.command.domain.values.TokenStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
@Slf4j
public class JwtPayloadEncoder {
    private static final String KEY = "f74fakfas2fsagfom3Axqweqrw1o2312";
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TYPE = "AES/CBC/PKCS5Padding";
    private static final byte[] IV = "DFSdgasghadfhadhe32rwgag".substring(0, 16).getBytes(); // Initialization Vector
    private static final SecretKey SECRET_KEY = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
    private static final Cipher CIPHER;
    private static final IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);

    static {
        try {
            CIPHER = Cipher.getInstance(CIPHER_TYPE);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public MemberJwtEncodedPayload encodedPayload(String memberId, Authority authority, TokenStatus tokenStatus) {

        String encodedMemberId = encode(memberId);
        String encodedAuthority = encode(authority.toString());
        String encodedTokenStatus = encode(tokenStatus.toString());

        return new MemberJwtEncodedPayload(encodedMemberId, encodedAuthority, encodedTokenStatus);
    }

    public MemberJwtDecodedPayload decodedPayload(MemberJwtEncodedPayload memberJwtEncodedPayload) {

        Long memberId = Long.valueOf(decode(memberJwtEncodedPayload.getMemberId()));
        Authority authority = Authority.valueOf(decode(memberJwtEncodedPayload.getAuthority()));
        TokenStatus tokenStatus = TokenStatus.valueOf(decode(memberJwtEncodedPayload.getStatus()));

        return new MemberJwtDecodedPayload(memberId, authority, tokenStatus);
    }

    public static String encode(String payloadValue) {

        try {
            CIPHER.init(Cipher.ENCRYPT_MODE, SECRET_KEY, ivParameterSpec);
        } catch (InvalidKeyException ignored) {
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        byte[] encrypted;
        try {
            encrypted = CIPHER.doFinal(payloadValue.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return new String(Base64.getEncoder().encode(encrypted));
    }

    public String decode(String payloadValue) {

        try {
            CIPHER.init(Cipher.DECRYPT_MODE, SECRET_KEY, ivParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        try {
            return new String(CIPHER.doFinal(Base64.getDecoder().decode(payloadValue)), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
