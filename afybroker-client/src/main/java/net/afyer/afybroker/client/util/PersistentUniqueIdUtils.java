package net.afyer.afybroker.client.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Persist a unique id for the current program path in the system temp directory.
 *
 * @author Nipuru
 * @since 2026/4/14 16:26
 */
public final class PersistentUniqueIdUtils {

    private static final String TEMP_DIRECTORY_NAME = "afybroker-unique-id";

    private PersistentUniqueIdUtils() {
    }

    public static String getOrCreateUniqueId(Class<?> anchorClass) {
        Path programPath = resolveProgramPath(anchorClass);
        Path idFile = resolveIdFile(programPath);
        try {
            Files.createDirectories(idFile.getParent());
            if (Files.exists(idFile)) {
                String existing = new String(Files.readAllBytes(idFile), StandardCharsets.UTF_8).trim();
                if (!existing.isEmpty()) {
                    return existing;
                }
            }

            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            Files.write(idFile, uniqueId.getBytes(StandardCharsets.UTF_8));
            return uniqueId;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load persistent unique id from " + idFile, e);
        }
    }

    private static Path resolveProgramPath(Class<?> anchorClass) {
        try {
            return Paths.get(anchorClass.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to resolve program path for " + anchorClass.getName(), e);
        }
    }

    private static Path resolveIdFile(Path programPath) {
        String tempDir = System.getProperty("java.io.tmpdir");
        return Paths.get(tempDir)
                .resolve(TEMP_DIRECTORY_NAME)
                .resolve(sha256(programPath.toString()));
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte current : bytes) {
                builder.append(String.format("%02x", current));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
