package core.access.iterator;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.common.collect.Lists;

import core.adapt.HDFSPartition;
import core.adapt.Partition;
import core.adapt.Predicate;
import core.adapt.Predicate.PREDTYPE;
import core.adapt.Query;
import core.utils.ConfUtils;
import core.utils.HDFSUtils;
import core.utils.TypeUtils.*;

public class TestHDFSScanIterator extends TestScanIterator {

	String propertiesFile;

	@Override
	public void setUp() {
		propertiesFile = "/Users/alekh/Work/MDIndex/conf/cartilage.properties";
		partitionDir = "hdfs://localhost:9000/user/alekh/dodo";
		int attributeIdx = 0;
		// Range r = RangeUtils.closed(3000000, 6000000);
		Predicate p1 = new Predicate(attributeIdx, TYPE.INT, 3000000,
				PREDTYPE.GEQ);
		Predicate p2 = new Predicate(attributeIdx, TYPE.INT, 6000000,
				PREDTYPE.LEQ);
		query = new Query("afds", new Predicate[] { p1, p2 });

		partitionPaths = Lists.newArrayList();
		ConfUtils cfg = new ConfUtils(propertiesFile);
		FileSystem hdfs = HDFSUtils.getFS(cfg.getHADOOP_HOME()
				+ "/etc/hadoop/core-site.xml");
		try {
			for (FileStatus fileStatus : hdfs
					.listStatus(new Path(partitionDir))) {
				String name = fileStatus.getPath().getName();
				try {
					Integer.parseInt(name);
					if (fileStatus.isFile()
							&& !fileStatus.getPath().getName().startsWith("."))
						partitionPaths.add("hdfs://localhost:9000"
								+ fileStatus.getPath().toUri().getPath()); // Do
																			// something
																			// with
																			// child
				} catch (Exception e) {
				}
			}
		} catch (IOException e) {
			System.out.println("No files to repartition");
		}
	}

	@Override
	protected Partition getPartitionInstance(String path) {
		return new HDFSPartition(path, propertiesFile, (short) 1);
	}

	@Override
	public void testScan() {
		super.testScan();
	}

	@Override
	public void testScanAll() {
		super.testScanAll();
	}
}
