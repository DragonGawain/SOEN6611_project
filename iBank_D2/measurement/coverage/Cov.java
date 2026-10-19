import org.jacoco.core.analysis.*; import org.jacoco.core.tools.ExecFileLoader; import java.io.File; import java.util.*;
public class Cov {
  public static void main(String[] a) throws Exception {
    ExecFileLoader l = new ExecFileLoader(); l.load(new File(a[0]));
    CoverageBuilder b = new CoverageBuilder();
    new Analyzer(l.getExecutionDataStore(), b).analyzeAll(new File(a[1]));
    Map<String,int[]> pk = new TreeMap<>(); int lc=0, lt=0, bc=0, bt=0;
    List<String> zero = new ArrayList<>();
    for (IClassCoverage c : b.getClasses()) {
      ICounter li = c.getLineCounter(), br = c.getBranchCounter();
      int[] v = pk.computeIfAbsent(c.getPackageName(), k -> new int[4]);
      v[0]+=li.getCoveredCount(); v[1]+=li.getTotalCount(); v[2]+=br.getCoveredCount(); v[3]+=br.getTotalCount();
      lc+=li.getCoveredCount(); lt+=li.getTotalCount(); bc+=br.getCoveredCount(); bt+=br.getTotalCount();
      if (!c.getPackageName().equals("ibank/ui") && li.getMissedCount() > 0) {
        for (IMethodCoverage m : c.getMethods()) if (m.getLineCounter().getCoveredCount()==0 && m.getLineCounter().getTotalCount()>0) zero.add(c.getName().replace("ibank/","")+"."+m.getName());
      }
    }
    pk.forEach((k,v)-> System.out.printf("%-14s lines %4d/%4d %3.0f%%  branches %3d/%3d%n", k, v[0], v[1], 100.0*v[0]/v[1], v[2], v[3]));
    System.out.printf("TOTAL lines %d/%d %.1f%% branches %d/%d %.1f%%%n", lc, lt, 100.0*lc/lt, bc, bt, 100.0*bc/bt);
    System.out.println("untested methods outside ui: " + zero);
  }
}
