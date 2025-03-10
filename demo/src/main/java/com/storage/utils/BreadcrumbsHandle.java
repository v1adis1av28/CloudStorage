package com.storage.utils;


import com.storage.model.BreadCrumb;
import java.util.ArrayList;

public class BreadcrumbsHandle {

    public static ArrayList<BreadCrumb> createBreadcrumbsByPath(String path) {
        ArrayList<BreadCrumb> arrBc = new ArrayList<>();
        String[] breadcrumbs = path.split("/");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < breadcrumbs.length; i++) {
            if (!breadcrumbs[i].isEmpty()) {
                builder.append(breadcrumbs[i]);
                if (i != breadcrumbs.length - 1) {
                    builder.append('/');
                }

                String currentPath = builder.toString();
                String parentPath = i == 0 ? "" : currentPath.substring(0, currentPath.lastIndexOf('/'));

                BreadCrumb bc = new BreadCrumb(parentPath, breadcrumbs[i]);
                arrBc.add(bc);
            }
        }

        return arrBc;
    }
}
