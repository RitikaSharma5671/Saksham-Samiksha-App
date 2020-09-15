package org.odk.collect.android.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;

import org.odk.collect.android.formmanagement.ServerFormDetails;
import org.odk.collect.android.openrosa.OpenRosaXmlFetcher;
import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.provider.FormsProvider;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowEnvironment;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.Map;

import static android.os.Environment.MEDIA_MOUNTED;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class FormListDownloaderTest {

    private final OpenRosaXmlFetcher serverClient = mock(OpenRosaXmlFetcher.class);

    @Before
    public void setup() {
        ShadowEnvironment.setExternalStorageState(MEDIA_MOUNTED); // Required for ODK directories to be created
    }

    @After
    public void teardown() {
        FormsProvider.releaseDatabaseHelper();
    }

    @Test
    public void shouldProcessAndReturnAFormList() throws Exception {
        Document doc = new Document();
        KXmlParser parser = new KXmlParser();
        parser.setInput(new StringReader(RESPONSE));
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        doc.parse(parser);

        configAppFor("http://example.com");
        when(serverClient.getXML("http://example.com/formList"))
                .thenReturn(new DocumentFetchResult(doc, true, "blah"));

        FormListDownloader downloader = new FormListDownloader(
                RuntimeEnvironment.application,
                serverClient,
                new WebCredentialsUtils()
        );

        final Map<String, ServerFormDetails> fetched = downloader.downloadFormList(null, null, null);
        assertEquals(2, fetched.size());

        ServerFormDetails f1 = fetched.get("one");
        assertNull(f1.getErrorStr());
        assertEquals("The First Form", f1.getFormName());
        assertEquals("https://example.com/formXml?formId=one", f1.getDownloadUrl());
        assertNull(f1.getManifestUrl());
        assertEquals("one", f1.getFormId());
        assertNull(f1.getFormVersion());
        assertFalse(f1.isUpdated());

        ServerFormDetails f2 = fetched.get("two");
        assertNull(f2.getErrorStr());
        assertEquals("The Second Form", f2.getFormName());
        assertEquals("https://example.com/formXml?formId=two", f2.getDownloadUrl());
        assertNull(f2.getManifestUrl());
        assertEquals("two", f2.getFormId());
        assertNull(f2.getFormVersion());
        assertFalse(f1.isUpdated());
    }

    @Test
    public void removesTrailingSlashesFromUrl() {
        when(serverClient.getXML(any())).thenReturn(new DocumentFetchResult("blah", 200));

        FormListDownloader serverFormsDetailsFetcher = new FormListDownloader(
                RuntimeEnvironment.application,
                serverClient,
                new WebCredentialsUtils()
        );

        serverFormsDetailsFetcher.downloadFormList("http://blah.com///", "user", "password");
        verify(serverClient).getXML("http://blah.com/formList");
    }

    private static void configAppFor(String url) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(Collect1.getInstance().getBaseContext()).edit();
        prefs.putString(GeneralKeys.KEY_SERVER_URL, url);
        if (!prefs.commit()) {
            throw new RuntimeException("Failed to set up SharedPreferences for MockWebServer");
        }
    }

    private static String join(String... strings) {
        StringBuilder bob = new StringBuilder();
        for (String s : strings) {
            bob.append(s).append('\n');
        }
        return bob.toString();
    }

    private static final String RESPONSE = join(
            "<xforms xmlns=\"http://openrosa.org/xforms/xformsList\">",
            "<xform><formID>one</formID>",
            "<name>The First Form</name>",
            "<majorMinorVersion></majorMinorVersion>",
            "<version></version>",
            "<hash>md5:b71c92bec48730119eab982044a8adff</hash>",
            "<downloadUrl>https://example.com/formXml?formId=one</downloadUrl>",
            "</xform>",
            "<xform><formID>two</formID>",
            "<name>The Second Form</name>",
            "<majorMinorVersion></majorMinorVersion>",
            "<version></version>",
            "<hash>md5:4428adffbbec48771c9230119eab9820</hash>",
            "<downloadUrl>https://example.com/formXml?formId=two</downloadUrl>",
            "</xform>",
            "</xforms>");
}