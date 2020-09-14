package com.samagra.cascading_module.tasks;

import android.os.AsyncTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.samagra.cascading_module.CascadingModuleDriver;
import com.samagra.commons.InstitutionInfo;
import com.samagra.grove.logging.Grove;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.xdrop.fuzzywuzzy.FuzzySearch;

// TODO : Add Documentation
public class SearchSchoolTask extends AsyncTask<String, Void, List<InstitutionInfo>> {

    String searchString;

    public SearchSchoolTask(String searchString) {
        this.searchString = searchString;
    }

    @Override
    protected List<InstitutionInfo> doInBackground(String... strings) {
        Grove.e("Starting the search");

        // Load data file and convert to a string of search values
//        //Collect.ODK_ROOT
        File dataFile = new File(CascadingModuleDriver.ROOT + "/saksham_data_json.json");
        try {
            JsonReader reader = new JsonReader(new FileReader(dataFile));

            Gson gson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .setVersion(1.0)
                    .create();

            Type listType = new TypeToken<ArrayList<InstitutionInfo>>() {
            }.getType();
            List<InstitutionInfo> data = gson.fromJson(reader, listType);
            Grove.e("%s", data.size());

            ArrayList<String> districts = new ArrayList<>();
            ArrayList<String> blocks = new ArrayList<>();
            ArrayList<String> schools = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                districts.add(data.get(i).getDistrict());
                blocks.add(data.get(i).getBlock());
                schools.add(data.get(i).getSchoolName());
            }

            districts = makeUnique(districts);
            blocks = makeUnique(blocks);
            schools = makeUnique(schools);

            Grove.e("Calculating top results");
            List results1 = FuzzySearch.extractTop(searchString, districts, 10);
            Grove.e("Extract top results found: %s", results1.toString());
            List results2 = FuzzySearch.extractTop(searchString, blocks, 10);
            Grove.e("Extract top results found: %s", results2.toString());
            List results3 = FuzzySearch.extractTop(searchString, schools, 10);
            Grove.e("Extract top results found: %s", results3.toString());

            double[] ratios = new double[data.size()];
            ArrayList<Double> ratiosFiltered = new ArrayList<Double>();

            for (int i = 0; i < data.size(); i++) {
                int ratio = FuzzySearch.tokenSetPartialRatio(searchString, data.get(i).getStringForSearch());
                ratios[i] = ratio;
            }
            Grove.e("Ratios Calculated");
        } catch (Exception e) {
            Grove.e(e);
        }

        return null;
    }

    public static ArrayList<String> makeUnique(ArrayList<String> districts) {
        Set<String> set = new HashSet<>(districts);
        districts.clear();
        districts.addAll(set);
        Collections.sort(districts);
        return districts;
    }
}
