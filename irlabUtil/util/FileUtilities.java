package edu.ucla.astro.irlab.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.io.IOException;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * <p>Title: FileUtilities/p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

/* some notes about java.io.File:
    example filename: /root/dir/subdir/file.ext

    getPath()
    getAbsolutePath()
    getCanonicalPath()
    toString() =          /root/dir/subdir/file.ext

    getName() = file.ext
    getParent() = /root/dir/subdir

*/
public class FileUtilities {

	//. make constructor private.  should just use statically.
  private FileUtilities() {
  }
  public static File addExtension(File file, String ext) throws SecurityException {
    return new File(file.getAbsolutePath()+"."+ext);
  }
  public static File addExtensionIfNone(File file, String ext) throws SecurityException {
  	if (file.getName().indexOf(".") == -1) {
  		return new File(file.getAbsolutePath()+"."+ext);
  	} else {
  		return file;
  	}
  }
  public static File renameWithExtension(File file, String ext) throws IOException {
    if (!file.renameTo(new File(file.getAbsolutePath()+"."+ext)))
      throw new IOException("Error adding extension. Could not rename file.");
    else
      return file;
  }
  public static File renameWithoutExtension(File file) throws IOException {
    String filename;
    int lastDot;

    filename=file.getPath();
    lastDot = filename.lastIndexOf(".");
    if (lastDot > 0 ) {
      if (!file.renameTo(new File(file.getPath().substring(0, lastDot))))
        throw new IOException("Error removing extension. Could not rename file.");
    }
    return file;
  }
  public static String getExtension(File file) {
    String filename;
    int lastDot;

    filename=file.getName();
    lastDot = filename.lastIndexOf(".");
    if (lastDot < 0 )
      return null;
    else if (lastDot == filename.length()-1)
      return "";
    else
      return filename.substring(lastDot+1, filename.length());
  }
  public static String[] getDirectoryTree(File file) {
    //. if file is /root/dir/subdir/file.ext,
    //. returns {"root", "dir", "subdir"};
  	return file.getAbsolutePath().split(File.separator);
 }
  public static String getNameOfFile(String filename) {
    return getNameOfFile(new File(filename));
  }
  public static String getNameOfFile(File file) {
    return file.getName();
  }
  public static String getDirectory(String filename) {
    return getDirectory(new File(filename));
  }
  public static String getDirectory(File file) {
    return file.getParent();
  }
  public static String replaceEnvironmentVariables(String filename) throws InvalidEnvironmentVariableException {
  	StringBuffer path = new StringBuffer(filename);
  	
	  //. replace leading tilde with user home dir
	  if (path.charAt(0) == '~') {
	      path=path.replace(0, 1, System.getProperty("user.home"));
	  }
  	
	  int envVarStart;
	  int envVarEnd;
  	//. environment variables must be enclosed in braces
  	while ((envVarStart = path.indexOf("${")) != -1) {
  		envVarEnd = path.indexOf("}");
  		if (envVarEnd < envVarStart) {
  			break;
  		}
  		StringBuffer envvarBraces = new StringBuffer(path.substring(envVarStart, envVarEnd));
  		envvarBraces.delete(0, 2);
  		String envvar = envvarBraces.toString();
  		String envvarValue = System.getenv(envvar);
  		
  		if (envvarValue == null) {
  			throw new InvalidEnvironmentVariableException("Environment variable <"+envvar+"> not defined.");
  		}
  		path.replace(envVarStart, envVarEnd+1, envvarValue);
  		
  	}
	  
  	//. on error, return original filename
  	
  	
  	return path.toString();
  }
  
	public static boolean confirmOrCreateDirectory(File directory, Component messageParent) throws IOException {
		if (!directory.exists()) {
			if (JOptionPane.showConfirmDialog(messageParent, "Directory "+directory+" does not exist?  Create?", "Create Directory?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
				return false;
			}
			if (!directory.mkdir()) {
				throw new IOException("Directory "+directory+" could not be created.");
			}
		}
		if (!directory.canWrite()) {
			throw new IOException("Directory "+directory+" is not writeable.");
		}
		return true;
	}
	
  public static class StandardFileFilter extends FileFilter {
  	private String[] extensions;
  	private String description = "";
  	public StandardFileFilter(String[] extensions) {
  		this(extensions, "");
  	}
  	public StandardFileFilter(String[] extensions, String description) {
  		this.extensions = extensions;
  		StringBuffer desc = new StringBuffer(description);
  		if (!description.isEmpty()) {
  			desc.append(" ");
  		}
  		desc.append("(");
  		for (String ext : extensions) {
  			desc.append("*.");
  			desc.append(ext);
  			desc.append(", *.");
  			desc.append(ext.toUpperCase());
  			desc.append(", ");
  		}
  		desc.delete(desc.length()-2, desc.length());
  		desc.append(")");
  		this.description = desc.toString();
  	}
  	public StandardFileFilter(String ext) {
    	this(ext, "");
  	}
    public StandardFileFilter(String ext, String description) {
    	this(new String[] {ext}, description);
    }
		@Override
		public boolean accept(File pathname) {
			String pathExtension = FileUtilities.getExtension(pathname);
			if (pathname.isDirectory()) return true;
			for (String ext : extensions) {
				if (pathExtension == null) {
					return false;
				}
				if (pathExtension.compareToIgnoreCase(ext) == 0) {
					return true;
				}
			} 
			return false;
		}
		public String getDescription() {
			return description;
		}
  }
	
  /* main just for testing */
  public static void main(String[] args) {
//    String name = "/net/highz/kroot/krootdev/osrsdev/kroot/kss/osiris/gui/util/OsirisFileUtils.java";
/*  	String name = "/u/mosdev/test.file";
  	File file = new File(name);

    System.out.println(File.separator);
    System.out.println(file.getAbsolutePath());
    
    String[] tree = getDirectoryTree(file);
    for (int ii=0; ii<tree.length; ii++) {
    	System.out.println(tree[ii]);
    }
    
    try {
    	File f2 = renameWithoutExtension(file);
    	System.out.println(f2.toString());
    	File f3 = renameWithoutExtension(f2);
    	System.out.println(f3.toString());
    } catch (Exception ex) {
    	ex.printStackTrace();
    }
  }
  */
  	String name="${KROO}/${MPRS_HOST}/test.fits";
  	try {
			System.out.println(FileUtilities.replaceEnvironmentVariables(name));
		} catch (InvalidEnvironmentVariableException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
  }
  	
  	
}