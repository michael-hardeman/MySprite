
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  Strings.java                                                                                                      //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//                                                                                                                    //
//  MySprite Specific Gui Components and data structures                                                              //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;

/////////////
// Strings //
/////////////
  public class Strings {

    /////////////////////////
    // levenshteinDistance //
    /////////////////////////
    public static int levenshteinDistance(String item1, String item2) {
      item1 = item1.toLowerCase();
      item2 = item2.toLowerCase();
      int[] costs = new int[item2.length() + 1];
      for(int i = 0; i <= item1.length(); i++) {
        int lastValue = i;
        for(int j = 0; j <= item2.length(); j++) {
          if(i == 0)
            costs[j] = j;
          else {
            if(j > 0) {
              int newValue = costs[j - 1];
              if(item1.charAt(i - 1) != item2.charAt(j - 1))
                newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
              costs[j - 1] = lastValue;
              lastValue = newValue;
            }
          }
        }
        if (i > 0)
          costs[item2.length()] = lastValue;
      }
      return costs[item2.length()];
    }
  }
