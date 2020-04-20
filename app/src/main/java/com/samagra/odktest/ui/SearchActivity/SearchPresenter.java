package com.samagra.odktest.ui.SearchActivity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.samagra.odktest.base.BasePresenter;
import com.samagra.odktest.data.models.School;
import com.samagra.odktest.helper.KeyboardHandler;
import com.samagra.odktest.tasks.SearchSchoolTask;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.contracts.IFormManagementContract;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.helpers.ContentResolverHelper;
import org.odk.collect.android.dto.Form;
import org.odk.collect.android.logic.FormInfo;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import timber.log.Timber;

/**
 * The Presenter class for Search Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link SearchMvpPresenter} and <b>must</b> be a type of {@link BasePresenter}.
 *
 * @author Pranav Sharma
 */
public class SearchPresenter<V extends SearchMvpView, I extends SearchMvpInteractor> extends BasePresenter<V, I> implements SearchMvpPresenter<V, I> {

    private List<School> schools = new ArrayList<>();

    @Inject
    public SearchPresenter(I mvpInteractor, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, iFormManagementContract);
    }

    //TODO : Make Asynchronous for less delay in loading
    @Override
    public void loadValuesToMemory(int selectedFormID) {
        Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, selectedFormID);
        String formPath = ContentResolverHelper.getFormPath(formUri);
        List<Form> allForms = getFormsFromDatabase();
        String formID = "";
        for(Form form: allForms){
            if(form.getFormFilePath().equals(formPath)){
                formID = form.getJrFormId();
            }
        }
        Timber.e(formID);
        File dataFile = new File(Collect.ODK_ROOT + "/data.json");
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(dataFile));

            Gson gson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .setVersion(1.0)
                    .create();

            Type type = new TypeToken<ArrayList<School>>() {
            }.getType();
            schools = gson.fromJson(jsonReader, type);
            addDummySchoolAtTheStart();
            jsonReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e("Exception in loading data to memory %s", e.getMessage());
        }
    }

    private Document updateDocumentTag(Document document, String tag, String value){
        try{
            if(document.getElementsByTagName(tag).item(0).getChildNodes().getLength() > 0)
                document.getElementsByTagName(tag).item(0).getChildNodes().item(0).setNodeValue(value);
            else
                document.getElementsByTagName(tag).item(0).appendChild(document.createTextNode(value));
        }catch (Exception e){
            Timber.e("Unable to autofill: %s %s", tag, value);
            return document;
        }
        return document;
    }

    @Override
    public void updateStarterFile(String formPath, School selectedSchool){
        String fileName = formPath;
        FileOutputStream fos = null;
        Timber.e("Autofilling: " + fileName);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Element element = document.getDocumentElement();
            String instanceId = element.getAttribute("instanceID");
            Timber.e("Form loaded successfully: " + instanceId);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getMvpView().getActivityContext());
            String designation = sharedPreferences.getString("user.designation", "");
            String userName = sharedPreferences.getString("user.username", "");
            String name = sharedPreferences.getString("user.fullName", "");

            updateDocumentTag(document, "district", selectedSchool.district);
            updateDocumentTag(document, "block", selectedSchool.block);
            updateDocumentTag(document, "username", userName);
            updateDocumentTag(document, "name", name);
            updateDocumentTag(document, "designation", designation);
            updateDocumentTag(document, "school", selectedSchool.schoolName);
            updateDocumentTag(document, "school_type", selectedSchool.schoolName.split(" ")[0]);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            fos = new FileOutputStream(new File(fileName));
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void addDummySchoolAtTheStart() {
        School dummy = new School(" Select District", " Select Block", " Select School");
        schools.add(0, dummy);
    }

    @Override
    public void addKeyboardListeners(KeyboardHandler keyboardHandler) {
        KeyboardVisibilityEvent.setEventListener((Activity) getMvpView().getActivityContext(), isOpen -> {
            keyboardHandler.isUDISEKeyboardShowing = isOpen;
            if (isOpen) keyboardHandler.closeDropDown();
        });
    }

    @Override
    public boolean isUDISEValid(String udise, String previousUdise) {
        Pattern testPattern = Pattern.compile("^[0-9]{10}$");
        Matcher testString = testPattern.matcher(udise);
        if (previousUdise != null)
            return testString.matches() && !previousUdise.equals(udise);
        else
            return testString.matches();
    }

    public ArrayList<String> getDistrictValues() {
        ArrayList<String> districtValues = new ArrayList<>();
        for (int i = 0; i < schools.size(); i++) {
            districtValues.add(schools.get(i).district);
        }
        return SearchSchoolTask.makeUnique(districtValues);
    }

    public ArrayList<String> getBlockValuesForSelectedDistrict(String district) {
        ArrayList<String> blockValues = new ArrayList<>();
        for (int i = 0; i < schools.size(); i++) {
            if (schools.get(i).district.equals(district)) {
                blockValues.add(schools.get(i).block);
            }
        }
        return SearchSchoolTask.makeUnique(blockValues);

    }

    ArrayList<String> getSchoolValuesForSelectedBlock(String selectedBlock, String selectedDistrict) {
        ArrayList<String> schoolValues = new ArrayList<>();
        for (int i=0; i<schools.size(); i++){
            if(schools.get(i).block.equals(selectedBlock) && schools.get(i).district.equals(selectedDistrict)){
                schoolValues.add(schools.get(i).schoolName);
            }
        }
        return SearchSchoolTask.makeUnique(schoolValues);
    }

    public School getSchoolObject(String selectedDistrict, String selectedBlock, String selectedSchoolName) {
        for (School school : schools) {
            if (school.district.equals(selectedDistrict)
                    && school.block.equals(selectedBlock)
                    && school.schoolName.equals(selectedSchoolName)
            ) return school;
        }
        return null;
    }

    @Override
    @NonNull
    public List<School> getSchoolList() {
        return schools;
    }

    public List<Form> getFormsFromDatabase() {
        FormsDao fd = new FormsDao();
        Cursor cursor = fd.getFormsCursor();
        return fd.getFormsFromCursor(cursor);
    }
}
