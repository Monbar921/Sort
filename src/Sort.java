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
  }

  public static class CommandArguments {
    boolean is_arg_valid = true;
    String sort_method = "d";
    String kind_of_data = "";
    String output_file = "";
    ArrayList<String> input_files = new ArrayList<>();

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
      Path out = Path.of(fileName);
      if (Files.exists(out)) {
        result = false;
        System.out.println("Output file already exist");
      } else {
        try{
          Files.createFile(out);
          this.output_file = fileName;
          Files.delete(out);
        } catch (IOException e){
          result = false;
          System.out.println("Cannot create output file");
        }
      }
      return result;
    }
  }
  public static void merge_sort(ArrayList<ReadFile> input_files, CommandArguments arg)
      throws IOException {
    if (input_files.size() > 1) {
      int count = 0;
      while (input_files.size() != 1) {
        String temp_result = "./temp_" + count + ".txt";
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

  public static void do_sort_in_while(
      BufferedReader bf1, BufferedReader bf2, CommandArguments arg, String temp_result)
      throws IOException {
    boolean is_need_read_1 = true;
    boolean is_need_read_2 = true;
    StringBuilder partOne = new StringBuilder();
    StringBuilder partTwo = new StringBuilder();
    while (true) {
      partOne = read_or_not(is_need_read_1, bf1, partOne);
      partTwo = read_or_not(is_need_read_2, bf2, partTwo);
      if (partOne == null && partTwo == null) break;
      StringBuilder resultLine;
      if (partOne == null) {
        is_need_read_1 = false;
        is_need_read_2 = true;
        resultLine = new StringBuilder(partTwo);
      } else if (partTwo == null) {
        is_need_read_1 = true;
        is_need_read_2 = false;
        resultLine = new StringBuilder(partOne);
      } else {
        SortIntAndStr sortIntAndStr =
            new SortIntAndStr(partOne, partTwo, arg, is_need_read_1, is_need_read_2);
        is_need_read_1 = sortIntAndStr.is_need_read_1;
        is_need_read_2 = sortIntAndStr.is_need_read_2;
        resultLine = sortIntAndStr.resultLine;
      }
      try (FileWriter fw = new FileWriter(temp_result, true);
          BufferedWriter bw = new BufferedWriter(fw);
          PrintWriter out = new PrintWriter(bw)) {
        out.println(resultLine.toString());
      }
    }
  }

  public static class SortIntAndStr {
    boolean is_need_read_1;
    boolean is_need_read_2;
    StringBuilder resultLine;

    public SortIntAndStr(
        StringBuilder partOne,
        StringBuilder partTwo,
        CommandArguments arg,
        boolean is_need_read_1,
        boolean is_need_read_2) {
      this.is_need_read_1 = is_need_read_1;
      this.is_need_read_2 = is_need_read_2;
      if (arg.kind_of_data.equals("i")) {
        sort_int(partOne, partTwo, arg);
      } else {
        sort_str(partOne, partTwo, arg);
      }
    }

    public void sort_int(StringBuilder partOne, StringBuilder partTwo, CommandArguments arg) {
      if (arg.sort_method.equals("a")) {
        if (Integer.parseInt(partOne.toString()) <= Integer.parseInt(partTwo.toString())) {
          this.resultLine = new StringBuilder(partOne);
          this.is_need_read_1 = true;
          this.is_need_read_2 = false;
        } else {
          this.resultLine = new StringBuilder(partTwo);
          this.is_need_read_1 = false;
          this.is_need_read_2 = true;
        }
      } else {
        if (Integer.parseInt(partOne.toString()) > Integer.parseInt(partTwo.toString())) {
          this.resultLine = new StringBuilder(partOne);
          this.is_need_read_1 = true;
          this.is_need_read_2 = false;
        } else {
          this.resultLine = new StringBuilder(partTwo);
          this.is_need_read_1 = false;
          this.is_need_read_2 = true;
        }
      }
    }

    public void sort_str(StringBuilder partOne, StringBuilder partTwo, CommandArguments arg) {
      int compare_value = partOne.toString().compareTo(partTwo.toString());
      if (arg.sort_method.equals("a")) {
        if (compare_value <= 0) {
          this.resultLine = new StringBuilder(partOne);
          this.is_need_read_1 = true;
          this.is_need_read_2 = false;
        } else {
          this.resultLine = new StringBuilder(partTwo);
          this.is_need_read_1 = false;
          this.is_need_read_2 = true;
        }
      } else {
        if (compare_value > 0) {
          this.resultLine = new StringBuilder(partOne);
          this.is_need_read_1 = true;
          this.is_need_read_2 = false;
        } else {
          this.resultLine = new StringBuilder(partTwo);
          this.is_need_read_1 = false;
          this.is_need_read_2 = true;
        }
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

  public static void delete_file(ReadFile file) throws IOException {
    if (file.is_need_sort) {
      Files.delete(Paths.get(file.sorted_file));
    }
  }

  public static void copy(String sourcePath, String destinationPath) throws IOException {
    Files.copy(Paths.get(sourcePath), new FileOutputStream(destinationPath));
  }


  public static class ReadFile {
    boolean is_need_sort = false;
    String sorted_file = "";

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
        create_files(input_file, arguments);
        do_sorted_file(input_file, arguments);
      }
    }

    boolean need_sort_int(BufferedReader br, CommandArguments arguments) throws IOException {
      boolean is_need_sort = false;
      String nowLine;
      int now_num;
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
      String nowLine;
      String previous_str = "";
      boolean is_start = true;
      while ((nowLine = br.readLine()) != null) {
        if (!is_start) {
          if (arguments.sort_method.equals("d")) {
            if (!(nowLine.compareTo(previous_str) <= 0)) {
              is_need_sort = true;
              break;
            }
          } else {
            if (!(nowLine.compareTo(previous_str) >= 0)) {
              is_need_sort = true;
              break;
            }
          }
        }
        if (is_start) {
          is_start = false;
        }
        previous_str = nowLine;
      }
      return is_need_sort;
    }

    void create_files(String input_file, CommandArguments arguments) throws IOException {
      String nowLine = "";
      String FILE_NAME = "./temp_directory/";
      int counter_files = 0;
      try (FileInputStream fstream = new FileInputStream(input_file)) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
          while ((nowLine = br.readLine()) != null) {
            if (arguments.kind_of_data.equals("i")) {
              try {
                Integer.parseInt(nowLine);
              } catch (NumberFormatException e) {
                continue;
              }
            }
            String next_filename = FILE_NAME + counter_files + ".txt";
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
      File folder = new File("./temp_directory");
      File[] listOfFiles = folder.listFiles();
      for (File file : listOfFiles) {
        if (file.isFile()) {
          files_in_folder.add(file.getAbsolutePath());
        }
      }
      while (files_in_folder.size() != 0) {
        int file_to_delete = 0;
        int min_max = 0;
        boolean is_start = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < files_in_folder.size(); i++) {
          Do_sort_in_file sort =
              new Do_sort_in_file(
                  files_in_folder, arguments, i, is_start, min_max, sb, file_to_delete);
          sb = sort.min_max_str;
          file_to_delete = sort.file_to_delete;
          min_max = sort.min_max;
          is_start = sort.is_start;
        }
        write_to_file(min_max, sb.toString(), arguments);
        files_in_folder.remove(file_to_delete);
      }
      deleteDir(folder);
    }

    void write_to_file(int min_max_num, String min_max_str, CommandArguments arguments)
        throws IOException {
      String to_write = min_max_num + "";
      if (arguments.kind_of_data.equals("s")) {
        to_write = min_max_str;
      }
      try (FileWriter fw = new FileWriter(this.sorted_file, true);
          BufferedWriter bw = new BufferedWriter(fw);
          PrintWriter out = new PrintWriter(bw)) {
        out.println(to_write);
      }
    }

    public static class Do_sort_in_file {
      int min_max;
      boolean is_start;
      int file_to_delete;
      StringBuilder min_max_str;

      public Do_sort_in_file(
          ArrayList<String> files_in_folder,
          CommandArguments arguments,
          int i,
          boolean is_start,
          int min_max,
          StringBuilder min_max_str,
          int file_to_delete)
          throws IOException {
        this.min_max = min_max;
        this.is_start = is_start;
        this.min_max_str = min_max_str;
        this.file_to_delete = file_to_delete;
        do_sort_in_input_file(files_in_folder, arguments, i);
      }

      void do_sort_in_input_file(
          ArrayList<String> files_in_folder, CommandArguments arguments, int i) throws IOException {
        try (FileInputStream fstream = new FileInputStream(files_in_folder.get(i))) {
          try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
              int current_number = 0;
              if (arguments.kind_of_data.equals("i")) {
                current_number = Integer.parseInt(nextLine);
                if (!this.is_start) {
                  if (arguments.sort_method.equals("a")) {
                    if (current_number <= this.min_max) {
                      this.min_max = current_number;
                      this.file_to_delete = i;
                    }
                  } else {
                    if (current_number >= this.min_max) {
                      this.min_max = current_number;
                      this.file_to_delete = i;
                    }
                  }
                }
              } else {
                if (!this.is_start) {
                  if (arguments.sort_method.equals("a")) {
                    if (nextLine.compareTo(min_max_str.toString()) <= 0) {
                      this.min_max_str = new StringBuilder(nextLine);
                      this.file_to_delete = i;
                    }
                  } else {
                    if (nextLine.compareTo(min_max_str.toString()) >= 0) {
                      this.min_max_str = new StringBuilder(nextLine);
                      this.file_to_delete = i;
                    }
                  }
                }
              }
              if (this.is_start) {
                this.is_start = false;
                this.min_max = current_number;
                this.min_max_str = new StringBuilder(nextLine);
              }
            }
          }
        }
      }
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
