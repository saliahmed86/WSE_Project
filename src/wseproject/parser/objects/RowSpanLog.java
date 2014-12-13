package wseproject.parser.objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ali Local
 */
public class RowSpanLog
    {
        int row;
        int howMany;
        int afterWhich;
        String contents;

        public RowSpanLog(int row, int howMany, int afterWhich, String contents)
        {
            this.row = row;
            this.howMany = howMany;
            this.afterWhich = afterWhich;
            this.contents = contents;
        }

    public int getRow()
    {
        return row;
    }

    public int getHowMany()
    {
        return howMany;
    }

    public int getAfterWhich()
    {
        return afterWhich;
    }

    public String getContents()
    {
        return contents;
    }
        
        

    }
