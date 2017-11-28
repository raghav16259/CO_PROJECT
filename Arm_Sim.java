import com.sun.org.apache.xalan.internal.xsltc.dom.LoadDocument;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Arm_Sim
{
    public static void main(String args[]) throws IOException
    {
        HashMap<String,String> mapbin=new HashMap<String, String>();
        HashMap<String,String> maphex=new HashMap<String, String>();
        InputStream is = new FileInputStream("in.txt");
        Reader.init(is);
        int c;
        String l;
        double registers[]=new double[16];

        while((l=Reader.reader.readLine())!=null)
        {
           String split[]=l.split(" ");
           String s="";
           for(int i=0;i<8;i++)
           {
               char x = split[1].charAt(2 + i);
               s=s+toBinary(x);
           }

           mapbin.put(split[0],s);
           maphex.put(split[0],split[1]);
           //System.out.println(s);
        }

        String address="0x0";
        int count=0;
        String instructions[]={"ADD","SUB","RSB","MUL","AND","ORR","EOR","MOV","MVN","CMP","LDR","LDI","STR","STI","B","BNE","BL","PRINT","EXIT"};
        while(!maphex.get(address).equals("0xEF000011"))
        {
            System.out.println("Fetch instruction "+maphex.get(address)+" from address "+address);
            count+=4;
            int op=decode(mapbin.get(address));
            System.out.print("DECODE: Operation is "+instructions[op]+", ");
            decode2(op,mapbin.get(address));
            String hex=Integer.toHexString(count);
            address="0x"+hex.toUpperCase();
            System.out.println();
        }
        System.out.println("Fetch instruction 0xEF000011 from address "+address);
        System.out.println("MEMORY: No memory operation");
        System.out.println("EXIT:");

    }

    public static void decode2(int index, String bin)
    {
        if(index<=13)
        {
            String firstop=bin.substring(12,16);
            int reg0=0;
            for(int i=0;i<firstop.length();i++)
            {
                if(firstop.charAt(i)=='1')
                    reg0=reg0+(int)Math.pow(2,firstop.length()-i-1);
            }

            System.out.print("First Operand is R"+reg0);

            if(index<=9)
            {
                if(bin.charAt(6)=='1')
                {
                    String secondop=bin.substring(25,32);
                    int reg1=0;
                    for(int i=0;i<secondop.length();i++)
                    {
                        if(secondop.charAt(i)=='1')
                            reg1=reg1+(int)Math.pow(2,secondop.length()-i-1);
                    }

                    System.out.println(", immediate Second Operand is "+reg1);
                }

                else
                {
                    String secondop=bin.substring(28,32);
                    int reg1=0;
                    for(int i=0;i<secondop.length();i++)
                    {
                        if(secondop.charAt(i)=='1')
                            reg1=reg1+(int)Math.pow(2,secondop.length()-i-1);
                    }

                    System.out.println(", Second Operand is R"+reg1);

                }
            }

            String destreg=bin.substring(16,20);
            int reg2=0;
            for(int i=0; i<destreg.length(); i++)
            {
                if(destreg.charAt(i)=='1')
                    reg2=reg2+(int)Math.pow(2,destreg.length()-i-1);
            }

            System.out.println("Destination Register is R"+reg2);



        }

    }

    public static String toBinary(char x)
    {
        switch(x)
        {
            case '0':
                return "0000";
            case '1':
                return "0001";
            case '2':
                return "0010";
            case '3':
                return "0011";
            case '4':
                return "0100";
            case '5':
                return "0101";
            case '6':
                return "0110";
            case '7':
                return "0111";
            case '8':
                return "1000";
            case '9':
                return "1001";
            case 'A':
                return "1010";
            case 'B':
                return "1011";
            case 'C':
                return "1100";
            case 'D':
                return "1101";
            case 'E':
                return "1110";
            case 'F':
                return "1111";
        }
        return null;
    }

    public static int decode(String bin)
    {
        String format=bin.substring(4,6);
        String opcode=null;

        if(bin.equals("00011010111111111111111111111001")) //bne
        {
            return 15;
        }

        if(format.equals("00"))
        {
            opcode=bin.substring(7,11);
            switch (opcode)
            {
                case "0100":
                    return 0;
                case "0010":
                    return 1;
                case "0011":
                    return 2;
                case "0000":
                    return 3;
                //case "0000":
                // return 4;
                case "1100":
                    return 5;
                case "0001":
                    return 6;
                case "1101":
                    return 7;
                case "1111":
                    return 8;
                case "1010":
                    return 9;
            }

            System.out.println(opcode);
        }

        else if(format.equals("01"))
        {
            if(bin.charAt(11)=='1')
            {
               if(bin.charAt(6)=='1')
                   return 10;
               else
                   return 11;
            }
            else
            {
                if(bin.charAt(6)=='1')
                    return 12;
                else
                    return 13;
            }
        }

        else if(format.equals("10"))
        {
            if(bin.charAt(6)=='1')
            {
                if(bin.charAt(7)=='0')
                {
                    return 14;
                }

                else if(bin.charAt(7)=='1')
                {
                    return 16;
                }
            }
        }

        else if(bin.substring(4,8).equals("1111"))
        {
             if(bin.substring(24,32).equals("01101011"))
             {
                 return 17;
             }

             else if(bin.substring(24,32).equals("00010001"))
             {
                 return 18;
             }
        }

        return 0;
    }
}

class Reader {
    static BufferedReader reader;
    static StringTokenizer tokenizer;

    /** call this method to initialize reader for InputStream */
    static void init(InputStream input) {
        reader = new BufferedReader(
                new InputStreamReader(input) );
        tokenizer = new StringTokenizer("");
    }

    /** get next word */
    static String next() throws IOException {
        while ( ! tokenizer.hasMoreTokens() ) {
            //TODO add check for eof if necessary
            tokenizer = new StringTokenizer(
                    reader.readLine() );
        }
        return tokenizer.nextToken();
    }

    static int nextInt() throws IOException {
        return Integer.parseInt( next() );
    }

    static double nextDouble() throws IOException {
        return Double.parseDouble( next() );
    }
}