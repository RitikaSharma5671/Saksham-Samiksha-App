package com.psx.odktest.ui.SearchActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.psx.odktest.base.BasePresenter;
import com.psx.odktest.data.models.School;
import com.psx.odktest.helper.KeyboardHandler;
import com.psx.odktest.tasks.SearchSchoolTask;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.odk.collect.android.application.Collect;
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
    public SearchPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }

    //TODO : Make Asynchronous for less delay in loading
    @Override
    public void loadValuesToMemory() {
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

    @Override
    public void updateStarterFile(String formName, School selectedSchool) {
        String fileName = Collect.FORMS_PATH + File.separator + formName + ".xml";
        Timber.e(fileName);
        FileOutputStream fos = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Element element = document.getDocumentElement();
            String instanceId = element.getAttribute("instanceID");
            Timber.e("Form loaded successfully: %s", instanceId);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getMvpView().getActivityContext());
            String phoneNumber = sharedPreferences.getString("user.mobilePhone", "");
            String userName = sharedPreferences.getString("user.username", "");
            String name = sharedPreferences.getString("user.fullName", "");

            if (document.getElementsByTagName("district").item(0).getChildNodes().getLength() > 0) {
                document.getElementsByTagName("district").item(0).getChildNodes().item(0).setNodeValue(selectedSchool.district);
                document.getElementsByTagName("block").item(0).getChildNodes().item(0).setNodeValue(selectedSchool.block);
                document.getElementsByTagName("cluster").item(0).getChildNodes().item(0).setNodeValue(selectedSchool.cluster);
                document.getElementsByTagName("school-name").item(0).getChildNodes().item(0).setNodeValue(selectedSchool.schoolName);

                if (document.getElementsByTagName("Contact_Number").item(0).getChildNodes().getLength() > 0)
                    document.getElementsByTagName("Contact_Number").item(0).getChildNodes().item(0).setNodeValue(phoneNumber);
                else
                    document.getElementsByTagName("Contact_Number").item(0).appendChild(document.createTextNode(phoneNumber));

                if (document.getElementsByTagName("Name").item(0).getChildNodes().getLength() > 0)
                    document.getElementsByTagName("Name").item(0).getChildNodes().item(0).setNodeValue(name);
                else
                    document.getElementsByTagName("Name").item(0).appendChild(document.createTextNode(name));

                document.getElementsByTagName("User_Name").item(0).getChildNodes().item(0).setNodeValue(userName);

            } else {
                document.getElementsByTagName("district").item(0).appendChild(document.createTextNode(selectedSchool.district));
                document.getElementsByTagName("block").item(0).appendChild(document.createTextNode(selectedSchool.block));
                document.getElementsByTagName("cluster").item(0).appendChild(document.createTextNode(selectedSchool.cluster));
                document.getElementsByTagName("school-name").item(0).appendChild(document.createTextNode(selectedSchool.schoolName));
                document.getElementsByTagName("Contact_Number").item(0).appendChild(document.createTextNode(phoneNumber));
                document.getElementsByTagName("User_Name").item(0).appendChild(document.createTextNode(userName));
                document.getElementsByTagName("Name").item(0).appendChild(document.createTextNode(name));
            }

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
        } finally {
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
        School dummy = new School(" Select District", " Select Block", " Select Cluster", "", " Select School");
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

    @Override
    public ArrayList<String> getDistrictValues() {
        ArrayList<String> districtValues = new ArrayList<>();
        for (int i = 0; i < schools.size(); i++) {
            districtValues.add(schools.get(i).district);
        }
        return SearchSchoolTask.makeUnique(districtValues);
    }

    @Override
    public ArrayList<String> getBlockValuesForSelectedDistrict(String district) {
        ArrayList<String> blockValues = new ArrayList<>();
        for (int i = 0; i < schools.size(); i++) {
            if (schools.get(i).district.equals(district)) {
                blockValues.add(schools.get(i).block);
            }
        }
        return SearchSchoolTask.makeUnique(blockValues);

    }

    @Override
    public ArrayList<String> getClusterValuesForSelectedBlock(String selectedBlock) {
        ArrayList<String> blockValues = new ArrayList<>();
        for (int i = 0; i < schools.size(); i++) {
            if (schools.get(i).block.equals(selectedBlock)) {
                blockValues.add(schools.get(i).cluster);
            }
        }
        return SearchSchoolTask.makeUnique(blockValues);
    }

    @Override
    public ArrayList<String> getSchoolValuesForSelectedCluster(String selectedCluster) {
        ArrayList<String> blockValues = new ArrayList<>();
        for (int i = 0; i < schools.size(); i++) {
            if (schools.get(i).cluster.equals(selectedCluster)) {
                blockValues.add(schools.get(i).schoolName);
            }
        }
        return SearchSchoolTask.makeUnique(blockValues);
    }

    @Override
    public School getSchoolObject(String selectedDistrict, String selectedBlock, String selectedCluster, String selectedSchoolName) {
        for (School school : schools) {
            if (school.district.equals(selectedDistrict)
                    && school.block.equals(selectedBlock)
                    && school.cluster.equals(selectedCluster)
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
}
