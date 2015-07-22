package SpreadSheet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Dictionary;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.xeustechnologies.jtar.*;


/**
 * 
 * @author Fei Wang
 * @author Danyang Li
 * 
 */

public class WorkSheetGroup extends LinkedHashMap<String, WorkSheet> {


	private static final long serialVersionUID = 1L;

	public void save(File file) {

		String name = file.getName();
		String absPath = file.getAbsolutePath();
		if(absPath.contains(".ass2")) absPath = absPath.substring(0,absPath.indexOf(".ass2"));
		if(name.contains(".ass2")) name = name.substring(0,name.indexOf(".ass2"));
		
		new File(absPath).mkdirs();

		for (Object sheet : this.keySet()) {
			((WorkSheet) this.get(sheet)).save(new File(absPath + "/"+ sheet.toString()));
		}

		// Output file stream
		FileOutputStream dest;
		try {
			dest = new FileOutputStream(absPath + ".ass2");

			// Create a TarOutputStream
			TarOutputStream out = new TarOutputStream(new BufferedOutputStream(dest));

			// Files to tar
			File files = new File(absPath);
			for (File f : files.listFiles()) {
				out.putNextEntry(new TarEntry(f, "." + name + "/"+ f.getName()));
				BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f));
				int count;
				byte data[] = new byte[2048];
				while ((count = origin.read(data)) != -1) {
					out.write(data, 0, count);
				}
				out.flush();
				origin.close();
				f.delete();
			}
			files.delete();
			out.close();

		} catch (Exception e) {
			
		}
	}

	public WorkSheetGroup load(File file) {
		String dictName = file.getName();
		String absPath = file.getAbsolutePath();

		String destFolder = file.getParent();

		try {
			TarInputStream tis = new TarInputStream(new BufferedInputStream(new FileInputStream(absPath)));
			TarEntry entry;
			while ((entry = tis.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[2048];
				new File(destFolder + "/" + entry.getName()).getParentFile()
						.mkdirs();
				FileOutputStream fos = new FileOutputStream(destFolder + "/"
						+ entry.getName());
				BufferedOutputStream dest = new BufferedOutputStream(fos);

				while ((count = tis.read(data)) != -1) {
					dest.write(data, 0, count);
				}

				dest.flush();
				dest.close();
			}

			tis.close();
		} catch (Exception e) {
		
		}

		File files = new File(destFolder + "/."+ dictName.substring(0, dictName.length() - 5));
		for (File f : files.listFiles()) {
			if (!f.isDirectory()) {
				this.put(f.getName(), new WorkSheet().load(f));
			}
		}
		return this;
	}
}
