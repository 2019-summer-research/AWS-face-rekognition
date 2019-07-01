package apientry;

import api.DetectFaces;
import api.collections.CreateCollection;
import api.collections.DeleteCollection;
import api.collections.DescribeCollection;
import api.collections.ListCollections;
import api.faces.IndexFaces;
import api.faces.SearchFacesByImage;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class App {
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.err.println("Please provide at least one argument");
        }
        switch (args[0]){
            case "help":
                System.out.println("Command line arguments");
                System.out.println("detect-faces \"imagepath\"");
                System.out.println("Detects faces in image and draws bounding boxes");
                System.out.println();
                System.out.println("collection \"name\"");
                System.out.println("Creates a new collection");
                System.out.println();
                System.out.println("list-collections");
                System.out.println("List all collections");
                System.out.println();
                System.out.println("delete-collection \"name\"");
                System.out.println("Delete a new collection");
                System.out.println();
                System.out.println("describe-collection \"name\"");
                System.out.println("Describes a collection");
                System.out.println();
                System.out.println("index-faces \"name\" \"imagepath1\" \"imagepath2\" \"imagepathx\"  ");
                System.out.println("Adds faces to a collection");
                System.out.println();
                System.out.println("search-faces-by-image \"name\" \"imagepath\"");
                System.out.println("Search for a face in a collection");
                System.out.println();

                break;
            case "detect-faces":
                DetectFaces detectFaces = new DetectFaces();
                detectFaces.run(args);
                break;
            case "create-collection":
                CreateCollection cc = new CreateCollection();
                cc.run(args);
                break;
            case "list-collections":
                ListCollections lc = new ListCollections();
                lc.run(args);
                break;
            case "delete-collection":
                DeleteCollection dc = new DeleteCollection();
                dc.run(args);
                break;
            case "describe-collection":
                DescribeCollection descc = new DescribeCollection();
                descc.run(args);
                break;
            case "index-faces":
                IndexFaces indf = new IndexFaces();
                indf.run(args);
                break;
            case "search-faces-by-image":
                SearchFacesByImage sfbi = new SearchFacesByImage();
                sfbi.run(args);
                break;
            case "createclass":
                MakeClassViaDirectory();
                break;
            case "testimage":
                TestImage(args[1]);
                break;

            default:
                System.err.println("Unknown argument: " + args[0]);
                return;
        }

    }


    static void TestImage(String collection)
    {

        String[] args = new String[3];

        args[1] = collection;


        File faceFile = null;

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // Ensure that we're only selecting directories. Disabling the 'all files' option
        jfc.setDialogTitle("Select your class root directory");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);

        int returnValue = jfc.showOpenDialog(null);

        // Set the class directory to be the one that the user has selected
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            faceFile = jfc.getSelectedFile();
        }

        else {
            // There was an error with choosing a directory. Throw an exception.
            System.err.println("PANIC");
        }
        args[2] = faceFile.getPath();
        SearchFacesByImage sfbi = new SearchFacesByImage();
        sfbi.run(args);





    }

    static void MakeClassViaDirectory(){



        File rootDirectory = null;

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // Ensure that we're only selecting directories. Disabling the 'all files' option
        jfc.setDialogTitle("Select your class root directory");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);

        int returnValue = jfc.showOpenDialog(null);

        // Set the class directory to be the one that the user has selected
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            rootDirectory = jfc.getSelectedFile();
        }

        else {
            // There was an error with choosing a directory. Throw an exception.
            System.err.println("PANIC");
        }
        String[] args2 = new String[2];
        args2[0] = " ";
        args2[1] = rootDirectory.getName();

        CreateCollection cc = new CreateCollection();
        cc.run(args2);

        String[] format = new String[rootDirectory.listFiles().length+2];
        format[1] = rootDirectory.getName();
        int i = 2;
        for (File f : rootDirectory.listFiles()){
            format[i] = f.getAbsolutePath();
            i ++;
        }
        IndexFaces indf = new IndexFaces();
        indf.run(format);

        DescribeCollection descc = new DescribeCollection();
        descc.run(args2);


    }
}
