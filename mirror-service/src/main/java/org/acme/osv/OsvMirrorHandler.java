package org.acme.osv;

import kong.unirest.json.JSONObject;
import org.acme.client.OsvClient;
import org.acme.model.OsvAdvisory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.acme.util.FileUtil.deleteFileAndDir;

@ApplicationScoped
public class OsvMirrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsvMirrorHandler.class);
    private OsvClient client;
    List<OsvAdvisory> osvAdvisories;

    @Inject
    public OsvMirrorHandler(final OsvClient client) {
        this.client = client;
        this.osvAdvisories = new ArrayList<>();
    }

    public List<OsvAdvisory> performMirror(String ecosystem) throws IOException {
        Path ecosystemZip = client.downloadEcosystemZip(ecosystem);
        try (InputStream inputStream = new FileInputStream(ecosystemZip.toFile());
             ZipInputStream zipInput = new ZipInputStream(inputStream)) {
            unzipFolder(zipInput);
            deleteFileAndDir(ecosystemZip);
            return osvAdvisories;
        } catch (IOException e) {
            LOGGER.error("Exception found while reading from OSV: ", e);
        }
        return osvAdvisories;
    }

    private void unzipFolder(ZipInputStream zipIn) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(zipIn));
        OsvAdvisoryParser parser = new OsvAdvisoryParser();
        ZipEntry zipEntry = zipIn.getNextEntry();
        while (zipEntry != null) {

            String line = null;
            StringBuilder out = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            JSONObject json = new JSONObject(out.toString());
            final OsvAdvisory osvAdvisory = parser.parse(json);
            if (osvAdvisory != null) {
                osvAdvisories.add(osvAdvisory);
            }
            zipEntry = zipIn.getNextEntry();
            reader = new BufferedReader(new InputStreamReader(zipIn));
        }
        reader.close();
    }
}