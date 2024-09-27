package com.munecting.api.domain.oidc.keyManagement;

import com.munecting.api.domain.oidc.dto.OidcPublicKey;
import com.munecting.api.domain.oidc.dto.OidcPublicKeyList;
import com.munecting.api.global.error.exception.OidcException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.munecting.api.global.util.DecodeUtil.decodeBase64;

@Component
@Slf4j
public class PublicKeyProvider {

    public PublicKey generatePublicKey(final Map<String, String> header, final OidcPublicKeyList publicKeys) {
        OidcPublicKey publicKey = publicKeys.getMatchedKey( header.get("kid"), header.get("alg"));
        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(final OidcPublicKey publicKey) {
        byte[] nBytes = decodeBase64(publicKey.n());
        byte[] eBytes = decodeBase64(publicKey.e());

        // n, e값을 이용해서 공개키 생성
        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes), new BigInteger(1, eBytes));
        try {
            return KeyFactory.getInstance(publicKey.kty()).generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Failed to generate public key from provided key specification. kty: {}, n: {}, e: {}",
                    publicKey.kty(), publicKey.n(), publicKey.e(), e);
            throw new OidcException();
        }
    }
}
