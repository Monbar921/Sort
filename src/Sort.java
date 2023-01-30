import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Sort {
  public static void main(String[] args) throws IOException {
    CommandArguments arg = new CommandArguments(args);
    if (arg.is_arg_valid) {
      ArrayList<ReadFile> input_files = new ArrayList<>();
      for (String file_name : arg.input_files) {
        input_files.add(new ReadFile(file_name, arg));
      }
      merge_sort(input_files, arg);
    }

    System.out.println(arg.is_arg_valid);
    System.out.println(arg.sort_method + " " + arg.kind_of_data + " " + arg.output_file);
  }

  public static void merge_sort(ArrayList<ReadFile> input_files, CommandArguments arg)
      throws IOException {

    if (input_files.size() > 1) {

      int count = 0;
      while(input_files.size() != 1) {
        String temp_result = "./src/temp_"+count+".txt";
        Files.createFile(Path.of(temp_result));
        try (BufferedReader bf1 =
                new BufferedReader(new FileReader(input_files.get(0).sorted_file));
            BufferedReader bf2 =
                new BufferedReader(new FileReader(input_files.get(1).sorted_file))) {
          do_sort_in_while(bf1, bf2, arg, temp_result);
        }
        delete_file(input_files.get(0));
        delete_file(input_files.get(1));
        input_files.remove(0);
        input_files.remove(0);
        input_files.add(0, new ReadFile(temp_result, arg));
        input_files.get(0).is_need_sort = true;
        count++;
      }
    }
    copy(input_files.get(0).sorted_file, arg.output_file);
    delete_file(input_files.get(0));
  }

  public static void do_sort_in_while(BufferedReader bf1, BufferedReader bf2, CommandArguments arg, String temp_result)
      throws IOException {
    boolean is_need_read_1 = true;
    boolean is_need_read_2 = true;
    StringBuilder partOne = new StringBuilder();
    StringBuilder partTwo = new StringBuilder();
    while (true) {
      partOne = read_or_not(is_need_read_1, bf1, partOne);
      partTwo = read_or_not(is_need_read_2, bf2, partTwo);
      if (partOne == null && partTwo == null) break;
      StringBuilder resultLine = new StringBuilder();
      if (partOne == null) {
        is_need_read_1 = false;
        is_need_read_2 = true;
        resultLine = new StringBuilder(partTwo);
      } else if (partTwo == null) {
        is_need_read_1 = true;
        is_need_read_2 = false;
        resultLine = new StringBuilder(partOne);
      } else {
        if (arg.kind_of_data.equals("i")) {
          if (arg.sort_method.equals("a")) {
            if (Integer.parseInt(partOne.toString()) < Integer.parseInt(partTwo.toString())) {
              resultLine = new StringBuilder(partOne);
              is_need_read_1 = true;
              is_need_read_2 = false;
            } else {
              resultLine = new StringBuilder(partTwo);
              is_need_read_1 = false;
              is_need_read_2 = true;
            }
          }
        }
      }
      try (FileWriter fw = new FileWriter(temp_result, true);
           BufferedWriter bw = new BufferedWriter(fw);
           PrintWriter out = new PrintWriter(bw)) {
        out.println(resultLine.toString());
      }
    }
  }

  public static StringBuilder read_or_not(boolean is_need_read, BufferedReader bf, StringBuilder sb)
      throws IOException {
    if (is_need_read) {
      String str = bf.readLine();
      if (str == null) {
        sb = null;
      } else {
        sb = new StringBuilder(str);
      }
    }
    return sb;
  }

  public static void delete_file(ReadFile file) throws IOException{
    if(file.is_need_sort){
      Files.delete(Paths.get(file.sorted_file));
    }
  }

  public static void copy(String sourcePath, String destinationPath) throws IOException {
    Files.copy(Paths.get(sourcePath), new FileOutputStream(destinationPath));
  }

  static int find_max_value(ArrayList<ReadFile> input_files) {
    int result = 0;
    boolean is_start = true;
    for (ReadFile next : input_files) {
      if (!is_start) {
        if (next.number_of_lines >= result) {
          result = next.number_of_lines;
        }
      } else {
        is_start = false;
        result = next.number_of_lines;
      }
    }
    return result;
  }

  public static class CommandArguments {
    boolean is_arg_valid = true;
    String sort_method = "d";
    String kind_of_data = "";
    String output_file = "";
    ArrayList<String> input_files = new ArrayList<String>();

    public CommandArguments(String[] args) {
      check_on_valid_input(args);
      if (is_arg_valid) {
        Set<String> set = new HashSet<>(input_files);
        input_files.clear();
        input_files.addAll(set);
      }
    }

    private void check_on_valid_input(String[] args) {
      if (args.length < 2) {
        this.is_arg_valid = false;
      } else {
        // 1 arg
        boolean is_first_arg = false;
        if (args[0].equals("-d")) {
          this.sort_method = "d";
          is_first_arg = true;
        } else if (args[0].equals("-a")) {
          this.sort_method = "a";
          is_first_arg = true;
        } else if (args[0].equals("-s") || args[0].equals("-i")) {
          this.is_arg_valid = check_second_arg(args[0]);
        } else {
          this.is_arg_valid = false;
        }
        // 2 arg
        if (this.is_arg_valid) {
          if (is_first_arg) {
            if (args[1].equals("-s") || args[1].equals("-i")) {
              this.is_arg_valid = check_second_arg(args[1]);
            } else {
              this.is_arg_valid = false;
            }
          } else {
            this.is_arg_valid = check_out_file(args[1]);
          }
        }
        // 3 arg
        if (this.is_arg_valid && is_first_arg) {
          this.is_arg_valid = check_out_file(args[2]);
        }
        if (this.is_arg_valid) {
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

    private boolean check_second_arg(String second_Arg) {
      boolean output = true;
      if (second_Arg.equals("-s")) {
        this.kind_of_data = "s";
      } else if (second_Arg.equals("-i")) {
        this.kind_of_data = "i";
      } else {
        output = false;
      }
      return output;
    }

    private boolean check_out_file(String fileName) {
      boolean result = true;
      if (Files.exists(Path.of(fileName))) {
        result = false;
        System.out.println("Output file already exist");
      } else {
        this.output_file = fileName;
      }
      return result;
    }
  }

  public static class ReadFile {
    int number_of_lines;
    boolean is_need_sort = false;
    String sorted_file = "";
    ArrayList<Integer> content_of_file = new ArrayList<Integer>();
    //        String dirName = "C:\\Users\\Пк\\IdeaProjects\\123\\Sort\\src\\directory";
    String dirName = "./directory";

    public ReadFile(String input_file, CommandArguments arguments) {
      try {
        this.sorted_file = input_file;
        check_file_on_correct(input_file, arguments);
      } catch (IOException exception) {
        System.out.println("You wrote a wrong path to file " + input_file);
      }
    }

    void check_file_on_correct(String input_file, CommandArguments arguments) throws IOException {

      try (FileInputStream fstream = new FileInputStream(input_file)) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {

          if (arguments.kind_of_data.equals("i")) {
            this.is_need_sort = need_sort_int(br, arguments);

          } else {
            this.is_need_sort = need_sort_str(br, arguments);
          }
        }
      }

      String dirName = "./src/temp_directory";

      if (is_need_sort) {
        File theDir = new File(dirName);
        if (!theDir.exists()) {
          theDir.mkdirs();
        }
        create_files_int(input_file);
        do_sorted_file(input_file, arguments);
      }
      count_lines();
    }

    void count_lines() throws IOException {
      try (FileInputStream fis = new FileInputStream(this.sorted_file);
          BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
        String line;
        //        System.out.println(12324234 + this.sorted_file);
        while ((line = br.readLine()) != null) {
          this.number_of_lines++;
        }
      }
    }

    boolean need_sort_int(BufferedReader br, CommandArguments arguments) throws IOException {
      boolean is_need_sort = false;
      String nowLine = "";
      int now_num = 0;
      int previous_num = 0;
      boolean is_start = true;
      while ((nowLine = br.readLine()) != null) {
        try {
          now_num = Integer.parseInt(nowLine);
        } catch (NumberFormatException e) {
          is_need_sort = true;
          break;
        }

        if (!is_start) {
          if (arguments.sort_method.equals("d")) {
            if (!(now_num <= previous_num)) {
              is_need_sort = true;
              break;
            }
          } else {
            if (!(now_num >= previous_num)) {
              is_need_sort = true;
              break;
            }
          }
        }

        if (is_start) {
          is_start = false;
        }
        previous_num = now_num;
      }
      return is_need_sort;
    }

    boolean need_sort_str(BufferedReader br, CommandArguments arguments) throws IOException {
      boolean is_need_sort = false;
      //            String nowLine ="";
      //            int now_num = 0;
      //            int previous_num = 0;
      //            boolean is_start = true;
      //            while ((nowLine = br.readLine()) != null)   {
      //                try{
      //                    now_num = Integer.parseInt(nowLine);
      //                } catch (NumberFormatException e){
      //                    continue;
      //                }
      //
      //                if(!is_start){
      //                    if(arguments.sort_method.equals("d")){
      //                        if(!(now_num <= previous_num)){
      //                            is_need_sort = true;
      //                            break;
      //                        }
      //                    } else {
      //                        if(!(now_num >= previous_num)){
      //                            is_need_sort = true;
      //                            break;
      //                        }
      //                    }
      //                }
      //
      //                if(is_start){
      //                    is_start = false;
      //                }
      //                previous_num=now_num;
      //            }
      return is_need_sort;
    }

    void create_files_int(String input_file) throws IOException {
      String nowLine = "";
      String FILE_NAME = "./src/temp_directory/";
      int counter_files = 0;
      try (FileInputStream fstream = new FileInputStream(input_file)) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
          while ((nowLine = br.readLine()) != null) {
            int now_num = 0;
            try {
              now_num = Integer.parseInt(nowLine);
            } catch (NumberFormatException e) {
              continue;
            }

            String next_filename = FILE_NAME + counter_files + ".txt";
            //                        System.out.println(next_filename);
            counter_files++;
            Files.createFile(Path.of(next_filename));

            try (FileOutputStream fos = new FileOutputStream(next_filename)) {
              try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
                bw.write(nowLine);
              }
            }
          }
        }
      }
    }

    void do_sorted_file(String input_file, CommandArguments arguments) throws IOException {
      ArrayList<String> files_in_folder = new ArrayList<>();
      String[] fileArray = input_file.split("\\.");
      this.sorted_file = fileArray[fileArray.length - 2] + "_temp.txt";
      Files.createFile(Path.of(this.sorted_file));
      File folder = new File("./src/temp_directory");
      File[] listOfFiles = folder.listFiles();

      for (File file : listOfFiles) {
        if (file.isFile()) {
          files_in_folder.add(file.getAbsolutePath());
        }
      }

      if (arguments.kind_of_data.equals("i")) {
        if (arguments.sort_method.equals("a")) {

          while (files_in_folder.size() != 0) {
            int file_to_delete = 0;
            int min = 0;
            boolean is_start = true;
            int current_number = 0;
            for (int i = 0; i < files_in_folder.size(); i++) {
              try (FileInputStream fstream = new FileInputStream(files_in_folder.get(i))) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
                  String nextLine = "";
                  while ((nextLine = br.readLine()) != null) {
                    current_number = Integer.parseInt(nextLine);
                    if (!is_start) {
                      if (current_number <= min) {
                        min = current_number;
                        file_to_delete = i;
                      }
                    }
                    if (is_start) {
                      is_start = false;
                      min = current_number;
                    }
                  }
                }
              }
            }
            try (FileWriter fw = new FileWriter(this.sorted_file, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
              out.println(min + "");
            }
            files_in_folder.remove(file_to_delete);
          }
        }
      }
      deleteDir(folder);
    }

    public static void deleteDir(File dirFile) {
      if (dirFile.isDirectory()) {
        File[] dirs = dirFile.listFiles();
        for (File dir : dirs) {
          deleteDir(dir);
        }
      }
      dirFile.delete();
    }
  }
}
