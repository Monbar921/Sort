import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Sort {
    public static void main(String[] args) throws IOException{
//        ReadFile readFile = new ReadFile(args[3]);
        CommandArguments arg = new CommandArguments(args);
        if(arg.is_arg_valid){

        }
//        System.out.println(arg.is_arg_valid);
//        System.out.println(arg.sort_method + " " + arg.kind_of_data+ " " + arg.output_file);
//
//        for(String x : arg.input_files){
//            System.out.println(x);
//        }
//        String FILE_NAME = "./new.txt";
//        if(!Files.exists(Path.of(FILE_NAME))){
//            Files.createFile(Path.of(FILE_NAME));
//        }
//
//        Path newFilePath = Paths.get(FILE_NAME);
//        Files.createFile(newFilePath);

    }
    public static class CommandArguments{
        boolean is_arg_valid = true;
        String sort_method = "d";
        String kind_of_data = "";
        String output_file = "";
        ArrayList<String> input_files = new ArrayList<String>();
//-i -a out.txt "C:\Users\Пк\IdeaProjects\123\Sort\src\1.txt"
        public CommandArguments(String[] args){
            check_on_valid_input(args);

        }

        void check_on_valid_input(String[] args){
            if(args.length<2){
                this.is_arg_valid=false;
            } else {
                //1 arg
                boolean is_first_arg = false;
                if(args[0].equals("-d")){
                    this.sort_method="d";
                    is_first_arg =true;
                } else if(args[0].equals("-a")){
                    this.sort_method="a";
                    is_first_arg =true;
                } else if(args[0].equals("-s") || args[0].equals("-i")){
                    this.is_arg_valid = check_second_arg(args[0]);
                } else {
                    this.is_arg_valid = false;
                }
                // 2 arg
                if(this.is_arg_valid){
                    if(is_first_arg){
                        if(args[1].equals("-s") || args[1].equals("-i")){
                            this.is_arg_valid = check_second_arg(args[1]);
                        } else {
                            this.is_arg_valid =false;
                        }
                    } else {
                        this.is_arg_valid=check_out_file(args[1]);
                    }
                }
                // 3 arg
                if(this.is_arg_valid && is_first_arg){
                    this.is_arg_valid=check_out_file(args[2]);
                }
                if(this.is_arg_valid) {
                    int i = 3;
                    if (!is_first_arg) {
                        i = 2;
                    }
                    for (; i < args.length; i++) {
                        input_files.add(args[i]);
                    }
                }
            }
        }

        boolean check_second_arg(String second_Arg){
            boolean output = true;
            if(second_Arg.equals("-s")){
                this.kind_of_data="s";
            } else if(second_Arg.equals("-i")){
                this.kind_of_data="i";
            } else{
                output = false;
            }
            return output;
        }

        boolean check_out_file(String fileName){
            boolean result = true;
            if(Files.exists(Path.of(fileName))){
                result = false;
                System.out.println("Output file already exist");
            } else {
                this.output_file=fileName;
            }
            return result;
        }
    }


    public static class ReadFile{
        ArrayList<Integer> content_of_file = new ArrayList<Integer>();
//        String dirName = "C:\\Users\\Пк\\IdeaProjects\\123\\Sort\\src\\directory";
        String dirName = "./directory";
        public ReadFile(String arg, CommandArguments arguments){
            try {
                check_file_on_Correct(arg);
            }catch (IOException exception){
                System.out.println("You wrote a wrong path to file " + arg);
            }
        }
        void check_file_on_Correct(String arg) throws IOException {
            FileInputStream fstream = new FileInputStream(arg);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String nowLine ="";
            String previousLine ="";

            while ((nowLine = br.readLine()) != null)   {


                try{
                    int next_num = Integer.parseInt(nowLine);
                } catch ( NumberFormatException e){
                    continue;
                }


            }


            fstream.close();
            br.close();
        }
    }
}


//    void my_read(String arg) throws IOException {
//        FileInputStream fstream = new FileInputStream(arg);
//        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
//        File theDir = new File(dirName);
//        if (!theDir.exists()){
//            theDir.mkdirs();
//        }
//        String strLine ="";
//
//        while ((strLine = br.readLine()) != null)   {
//            int next_num = 0;
//
//            try{
//                next_num = Integer.parseInt(strLine);
//            } catch ( NumberFormatException e){
//                continue;
//            }
//            content_of_file.add(next_num);
//
//        }
//
//        for(Integer x : content_of_file){
//            System.out.println(x);
//        }
//
//        fstream.close();
//        br.close();
//    }