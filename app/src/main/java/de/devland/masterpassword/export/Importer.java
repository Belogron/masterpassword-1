package de.devland.masterpassword.export;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.util.RequestCodeManager;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by deekay on 26/10/14.
 */
public class Importer implements RequestCodeManager.RequestCodeCallback {
    public static final int REQUEST_CODE_IMPORT = 12345;
    public static final String EXTRA_IMPORT_TYPE = "de.devland.export.Exporter.IMPORT_TYPE";

    private Activity activity;

    public void startImportIntent(Activity activity, ImportType importType) {
        this.activity = activity;

        Intent getContentIntent = FileUtils.createGetContentIntent();
        getContentIntent.setType("text/plain");

        Intent intent = Intent.createChooser(getContentIntent, activity.getString(R.string.caption_selectFile));
        Bundle extraData = new Bundle();
        extraData.putSerializable(EXTRA_IMPORT_TYPE, importType);
        int requestCode = RequestCodeManager.INSTANCE
                .addRequest(REQUEST_CODE_IMPORT, this.getClass(), this, extraData);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void run(int resultCode, Intent intent, Bundle data) {
        if (resultCode == Activity.RESULT_OK) {
            ImportType importType = (ImportType) data.getSerializable(EXTRA_IMPORT_TYPE);
            ExportType exportType = ExportType.JSON; // TODO from url or data

            String contents = null;

            try {
                InputStream inputStream = activity.getContentResolver()
                        .openInputStream(intent.getData());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder fileContent = new StringBuilder("");
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line);
                }
                reader.close();
                inputStream.close();

                contents = fileContent.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Crouton.showText(activity, R.string.error_generic, Style.ALERT);
            }

            if (contents != null) {
                if (importType == ImportType.OVERRIDE) {
                    Site.deleteAll(Site.class);
                }
                List<Site> importedSites = new ArrayList<>();

                switch (exportType) {
                    case MPSITES:
                        // TODO
                        break;
                    case JSON:
                        List<Site> gsonSites;
                        Gson gson = new GsonBuilder()
                                .setPrettyPrinting()
                                .excludeFieldsWithoutExposeAnnotation()
                                .serializeNulls().create();
                        Type listType = new TypeToken<ArrayList<Site>>() {
                        }.getType();
                        gsonSites = gson.fromJson(contents, listType);
                        if (gsonSites != null) {
                            importedSites = gsonSites;
                        } else {
                            Crouton.showText(activity, R.string.error_generic, Style.ALERT);
                        }
                        break;
                    default:
                        Crouton.showText(activity, R.string.error_generic, Style.ALERT);
                        return;
                }


                for (Site site : importedSites) {
                    site.save();
                }

                Crouton.showText(activity, R.string.msg_importDone, Style.CONFIRM);
            }


        }
    }
}