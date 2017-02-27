
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  Strings.java                                                                                                      //
//  Michael Hardeman                                                                                                  //
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

////////////////////////////////////////////////////////////////////////////////
// MySprite - Java Sprite Editor                                              //
// Copyright (C) 2013 Michael Allen Hardeman                                  //
//                                                                            //
// This program is free software: you can redistribute it and/or modify       //
// it under the terms of the GNU General Public License as published by       //
// the Free Software Foundation, either version 3 of the License, or          //
// (at your option) any later version.                                        //
//                                                                            //
// This program is distributed in the hope that it will be useful,            //
// but WITHOUT ANY WARRANTY; without even the implied warranty of             //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              //
// GNU General Public License for more details.                               //
//                                                                            //
// You should have received a copy of the GNU General Public License          //
// along with this program.  If not, see <http://www.gnu.org/licenses/>.      //
////////////////////////////////////////////////////////////////////////////////
