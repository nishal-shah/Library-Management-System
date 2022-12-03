import java.io.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class LibraryDataBase
{
    public static HashMap<Integer,Book> listofBooks = new HashMap<Integer,Book>();  //Book ID vs Book Object
    public static HashMap<Integer,Student> listofStudent = new HashMap<Integer,Student>(); // Student ID vs Student Object
    public static long getDate()
    {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter Current Date in DDMMYYYY format : ");
        String date=sc.nextLine();
        SimpleDateFormat sdf= new SimpleDateFormat("ddMMyyyy");
        try
        {
           Date d1 = sdf.parse(date);
           long ans=(d1.getTime()/(1000*60*60*24));
           return ans;
        }
        catch (ParseException e) 
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static void printWithDelays(String data, TimeUnit unit, long delay) throws InterruptedException 
    {
        for (char ch:data.toCharArray()) 
        {
            System.out.print(ch);
            unit.sleep(delay);
        }
    }

    public static void writeStudentMap(String name)
	{
        try 
        {
            FileOutputStream fos=new FileOutputStream(new File(name+".txt"));
            PrintWriter pw=new PrintWriter(fos);
            for(Map.Entry<Integer,Student> m: LibraryDataBase.listofStudent.entrySet())
            {
                Student e=m.getValue();
                pw.print(m.getKey()+":"+e.uname+","+e.password+","+e.dues+","+e.max_books);
                if(e.issuedBook.size()>0)
                {
                    pw.print(":");
                    for(Map.Entry<Integer,Long> m1: e.issuedBook.entrySet())
                    {
                        pw.print(m1.getKey()+","+m1.getValue()+";");
                    }
                }
                pw.println();
            }
            pw.flush();
            pw.close();
            fos.close();
        }
      catch(Exception e) {}
	}

	public static void readStudentMap(String d) 
    {
        try
        {
            String line;
            BufferedReader reader=new BufferedReader(new FileReader(d+".txt"));
            while((line=reader.readLine())!=null)
            {
                String[] parts=line.split(":",3);
                String key=parts[0];
                String[] temp=parts[1].split(",",4);
                Student s=new Student(temp[0],temp[1],Integer.parseInt(key));
                s.dues=Integer.parseInt(temp[2]);
                s.max_books=Integer.parseInt(temp[3]);
                if(parts.length>=3)
                {
                    String[] temp1=parts[2].split(";",3);
                    for(int i=0;i<temp1.length;i++)
                    {
                        String[] temp2=temp1[i].split(",",2);
                        s.issuedBook.put(Integer.parseInt(temp2[0]),Long.parseLong(temp2[1]));
                    }
                }
             LibraryDataBase.listofStudent.put(Integer.parseInt(key),s);
            }
            reader.close();
        }
    catch(Exception e){}
    }

    public static void writeBookMap(String name)
	{
       try
        {
            FileOutputStream fos=new FileOutputStream(new File(name+".txt"));
            PrintWriter pw=new PrintWriter(fos);
            for(Map.Entry<Integer,Book> m: LibraryDataBase.listofBooks.entrySet())
            {
                Book e=m.getValue();
                pw.print(m.getKey()+":"+e.title+","+e.author+","+e.subject+","+e.publisher+","+e.edition+","+e.status+","+e.cid);
                pw.println();
            }
            pw.flush();
            pw.close();
            fos.close();
        }
    catch(Exception e){}
	}

    public static void readBookMap(String d) 
    {
        try
        {
        String line;
        BufferedReader reader=new BufferedReader(new FileReader(d+".txt"));
        while((line=reader.readLine())!=null)
        {
            String[] parts=line.split(":",2);
            String key=parts[0];
            String[] temp=parts[1].split(",",7);
            Book b=new Book(temp[0],temp[1],temp[2],temp[3],temp[4],Integer.parseInt(key));
            b.status=Boolean.parseBoolean(temp[5]);
            b.cid=Integer.parseInt(temp[6]);
        LibraryDataBase.listofBooks.put(Integer.parseInt(key),b);
    }
    reader.close();
    }
    catch(Exception e){}
    }

    public static void writeDuesVar(String name)
	{
    try {
        FileOutputStream fos=new FileOutputStream(new File(name+".txt"));
        PrintWriter pw=new PrintWriter(fos);
        pw.write("Dues Fee"+":"+LibraryDataBase.rate);
        pw.flush();
        pw.close();
        fos.close();
    }
    catch(Exception e) {}
	}

    public static void readDuesVar(String d) 
    {
        try
        {
        String line;
        BufferedReader reader=new BufferedReader(new FileReader(d+".txt"));
        while((line=reader.readLine())!=null)
        {
           String temp[]=line.split(":",2); 
           LibraryDataBase.rate=Integer.parseInt(temp[1]);
        }
         reader.close();
    }
    catch(Exception e){}
    }

    public static int rate=2;
}

class Book
{
    public int b_id;
    public String title;
    public String author;
    public String subject;
    public String publisher;
    public String edition;
    public boolean status;
    public int cid;
    public Book(String title,String author,String subject,String publisher,String edition,int book_id)  // Can be called only by librarian
    {
        this.title=title;
        this.author=author;
        this.subject=subject;
        this.publisher=publisher;
        this.edition=edition;
        this.status=true;
        this.b_id=book_id;
        this.cid=0;
        LibraryDataBase.listofBooks.put(book_id,this);
    }

    public void displayBookDetails()    
    {
       System.out.println("Book ID : " + this.b_id);
       System.out.println("Book Name : " + this.title);
       System.out.println("Book Author : " + this.author);
       System.out.println("Book Subject : " + this.subject);
       System.out.println("Book Publisher : " + this.publisher);
       System.out.println("Book Edition : " + this.edition);
       if(this.status==true)
       {
        System.out.println("Book Status : Available");
       }
       else
       {
        System.out.println("Book Status : Checked Out");
       }
    }
}

class User
{
    public String uname;
    public String password;
    User(String name,String password)
    {
        this.uname=name;
        this.password=password;
    }
}

class Student extends User
{
    public int s_id;
    public int max_books;
    public int dues;
    public HashMap<Integer,Long> issuedBook;  //Book ID vs Issue Date
    public Student(String name,String password,int id)  //Will be called while registering new student
    {
        super(name,password);
        this.max_books=0;
        this.dues=0;
        this.s_id=id;
        LibraryDataBase.listofStudent.put(id,this);
        this.issuedBook=new HashMap<Integer,Long>();
    }

    public void displayStudentDetails()  //Only by Librarian
    {
       System.out.println("Student ID : " + this.s_id);
       System.out.println("Student Name : " + this.uname);
       System.out.println("No of Books Issued : " + this.max_books);
       System.out.println("Student Dues : Rs. " + this.dues);  
       System.out.println("\n");
    }

    public void searchBook(String btitle,String bedition) throws InterruptedException //Student Version (does not show who checked it out if not available)
    {
        System.out.print("Searching for Book ");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,1000); 
		System.out.println("");
        int check=0;
        ArrayList<String>t=new ArrayList<String>();
        for(Map.Entry<Integer,Book> entry: LibraryDataBase.listofBooks.entrySet())
        {
            Book temp=entry.getValue();
            if(btitle.equalsIgnoreCase(temp.title) && bedition.equals(temp.edition))
            {
                System.out.println("\n");
                temp.displayBookDetails();
                System.out.println("\n");
                return;
            }
            else if(btitle.equals(temp.title))
            {
                check=1;
                t.add(temp.edition);
            }
        }
        if(check==1)
        {
            System.out.println("The book is available but with "+ t + " edition. Search with available edition to get its details\n");
            return;
        }
        System.out.println("The book is not available in the library\n");
    }
    
    public void issueBook(int id) throws InterruptedException 
    {
        if(this.max_books==3)
        {
            System.out.println("You cannot issue more than 3 books at a time\n");
            return;
        }
        if(!LibraryDataBase.listofBooks.containsKey(id))
        {
            System.out.println("Book with entered ID does not exist in library.\n");
            return;
        }
        if(this.issuedBook.containsKey(id))
        {
            System.out.println("You have already issued this book !\n");
            return;
        }
        Book temp=LibraryDataBase.listofBooks.get(id);
        if(temp.status==false)
        {
            System.out.println("Sorry ! The book has already been checked out.\n");
            return;
        }
        temp.cid=this.s_id;
        temp.status=false;
        long date=LibraryDataBase.getDate();
        this.issuedBook.put(id,date);    
        this.max_books++;
        System.out.print("Issuing Book ");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,650); 
		System.out.println("");
        System.out.println("Book issued successfully !! Due date is after 15 days\n");
    }

    public void renewBook(int id) throws InterruptedException
    {
        if(!LibraryDataBase.listofBooks.containsKey(id))
        {
            System.out.println("Book with entered ID does not exist in library.\n");
            return;
        }
        if(!this.issuedBook.containsKey(id))
        {
            System.out.println("You have currently not issued this book\n");
            return;
        }
        long date=LibraryDataBase.getDate();
        boolean dcheck=false;
        long d=0;
        long l=0;
        if(date-this.issuedBook.get(id)>14)
        {
            l=(date-this.issuedBook.get(id)-14);
            d=LibraryDataBase.rate*l;
            this.dues+=d;
            dcheck=true;
        }
        this.issuedBook.put(id,date);  
        System.out.print("Renewing Book ");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,650); 
		System.out.println("");
        if(dcheck==true)
        {
            System.out.println("Book renewed successfully !! Due date is after 15 days");
            System.out.println("You are late by "+l+" days. Dues of Rs. "+d+" charged.\n");
        }
        else
        {
            System.out.println("Book renewed successfully !! Due date is after 15 days\n");
        }
    }

    public void returnBook(int id) throws InterruptedException
    {
        if(!LibraryDataBase.listofBooks.containsKey(id))
        {
            System.out.println("Book with entered ID does not exist in library.\n");
            return;
        }
        if(!this.issuedBook.containsKey(id))
        {
            System.out.println("You have not issued this book\n");
            return;
        }
        Book temp=LibraryDataBase.listofBooks.get(id);
        temp.cid=0;
        temp.status=true;
        long date=LibraryDataBase.getDate();
        boolean dcheck=false;
        long d=0;
        long l=0;
        if(date-this.issuedBook.get(id)>14)
        {
            l=(date-this.issuedBook.get(id)-14);
            d=LibraryDataBase.rate*l;
            this.dues+=d;
            dcheck=true;
        }
        this.issuedBook.remove(id);
        this.max_books--;
        System.out.print("Returning Book ");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,650); 
		System.out.println("");
        if(dcheck==true)
        {
            System.out.println("Book returned succesfully");
            System.out.println("You are late by "+l+" days. Dues of Rs. "+d+" charged.\n");
        }
        else
        {
            System.out.println("Book returned succesfully\n");
        }
        return;
    }

    public void viewSubBook(String sub) throws InterruptedException
    {
        System.out.print("Searching");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,650); 
		System.out.println("\n");
        int check=0;
        for(Map.Entry<Integer,Book> entry: LibraryDataBase.listofBooks.entrySet())
        {
            Book temp=entry.getValue();
            if(temp.subject.equalsIgnoreCase(sub))
            {
                check=1;
                temp.displayBookDetails();
                System.out.println("");
            }
        }
        if(check==0)
        {
            System.out.println("No books on this subject\n");
        }
    }

    public void viewDues()
    {
        System.out.println("Student Dues Rs. " + this.dues +"\n");
    }
}

class Librarian extends User
{
    public Librarian(String name,String password)
    {
       super(name, password);
    }

    public void removeBook(int id)
    {
        if(!LibraryDataBase.listofBooks.containsKey(id))
        {
            System.out.println("Book with entered ID does not exist in Library\n");
            return;
        }
        Book temp=LibraryDataBase.listofBooks.get(id);
        if(temp.status==false)
        {
            System.out.println("The book has been checked out by Student "+ temp.cid +" so cannot be removed\n");
            return;
        }
        LibraryDataBase.listofBooks.remove(id);
        System.out.println("Book removed succesfully !\n");
        return;
    }

    public void searchBook(String btitle,String bedition) throws InterruptedException               
    {
        System.out.print("Searching for Book ");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,1000); 
		System.out.println("");
        int check=0;
        ArrayList<String>t=new ArrayList<String>();
        for(Map.Entry<Integer,Book> entry: LibraryDataBase.listofBooks.entrySet())
        {
            Book temp=entry.getValue();
            if(btitle.equalsIgnoreCase(temp.title) && bedition.equals(temp.edition))
            {
                System.out.println("\n");
                temp.displayBookDetails();
                if(temp.cid != 0)
                {
                    System.out.println("Book checked out by Student ID " + temp.cid);
                }
                System.out.println("\n");
                return;
            }
            else if(btitle.equalsIgnoreCase(temp.title))
            {
                check=1;
                t.add(temp.edition);
            }
        }
        if(check==1)
        {
            Collections.sort(t);
            System.out.println("The book is available but with edition "+ t + ". Search with available edition to get its details\n");
            return;
        }
        System.out.println("The book is not available in the library\n");
    }

    public void displayAllBooks() throws InterruptedException    
    {
        System.out.print("Fetching Details ");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,1000); 
		System.out.println("");   
        for(Map.Entry<Integer,Book> entry: LibraryDataBase.listofBooks.entrySet())
        {
            Book temp=entry.getValue();
            temp.displayBookDetails();
            if(temp.cid != 0)
            {
                System.out.println("Book checked out by Student ID " +temp.cid);
            }
            System.out.println("\n");
        } 
    }

    public void editBookDetails(int tid)
    {
        Scanner sc=new Scanner(System.in);
        if(!LibraryDataBase.listofBooks.containsKey(tid))
        {
            System.out.println("Book with entered ID does not exist\n");
            return;
        }
        Book temp=LibraryDataBase.listofBooks.get(tid);
        System.out.println("What do you wish to edit ?");
        System.out.println("1. Book Name");
        System.out.println("2. Book Author");
        System.out.println("3. Book Edition");
        System.out.println("4. Book Subject");
        System.out.println("5. Book Publisher");
        System.out.print("Enter your choice : ");
        int c=sc.nextInt();
        sc.nextLine();
        switch(c){
            case 1:
            {
                System.out.print("Enter new Book Name: ");
                String tem=sc.nextLine();
                temp.title=tem;
                System.out.println("New Book Details\n");
                temp.displayBookDetails();
                System.out.println("");
                return;
            }
            case 2:
            {
                System.out.print("Enter new Book Author: ");
                String tem=sc.nextLine();
                temp.author=tem;
                System.out.println("New Book Details\n");
                temp.displayBookDetails();
                System.out.println("");
                return;
            }
            case 3:
            {
                System.out.print("Enter new Book Edition: ");
                String tem=sc.nextLine();
                temp.edition=tem;
                System.out.println("New Book Details\n");
                temp.displayBookDetails();
                System.out.println("");
                return;
            }
            case 4:
            {
                System.out.print("Enter new Book Subject: ");
                String tem=sc.nextLine();
                temp.subject=tem;
                System.out.println("New Book Details\n");
                temp.displayBookDetails();
                System.out.println("");
                return;
            }
            case 5:
            {
                System.out.print("Enter new Book Publisher: ");
                String tem=sc.nextLine();
                temp.publisher=tem;
                System.out.println("New Book Details\n");
                temp.displayBookDetails();
                System.out.println("");
                return;
            }
            default:
            {
                System.out.println("Enter valid choice\n");
                return;
            }
        }
    }

    public void searchStudent(int id)
    {
        if(!LibraryDataBase.listofStudent.containsKey(id))
        {
            System.out.println("No student with entered ID\n");
            return;
        }
        Student temp=LibraryDataBase.listofStudent.get(id);
        temp.displayStudentDetails();
        return;
    }

    public void displayAllStudents() throws InterruptedException
    {
        System.out.print("Fetching Details ");
        LibraryDataBase.printWithDelays("...", TimeUnit.MILLISECONDS,1000); 
		System.out.println("");
        for(Map.Entry<Integer,Student> entry: LibraryDataBase.listofStudent.entrySet())
        {
            Student temp=entry.getValue();
            temp.displayStudentDetails();
        }  
    }

    public void editStudentDetails(int id)
    {
        Scanner sc=new Scanner(System.in);
        if(!LibraryDataBase.listofStudent.containsKey(id))
        {
            System.out.println("No Student with entered ID\n");
            return;
        }
        Student temp=LibraryDataBase.listofStudent.get(id);  
        System.out.println("What do you wish to do ?");  //Add settle dues
        System.out.println("1. Change Name");
        System.out.println("2. Change Password");
        System.out.print("Enter Choice : ");
        int c=sc.nextInt();
        sc.nextLine();
        switch(c){
            case 1:
            {
                System.out.print("Enter new name: ");
                String s=sc.nextLine();
                temp.uname=s;
                System.out.print("\n");
                System.out.print("New Student Details\n");
                temp.displayStudentDetails();
                return;
            }
            case 2:
            {
                System.out.print("Enter new Password: ");
                String s=sc.nextLine();
                temp.password=s;
                System.out.println("Password Updated\n");
                return;
            }
            default:
            {
                System.out.println("Enter Valid Choice");
                return;
            }
        }
    }

    public void settleDues(int id)
    {
        if(!LibraryDataBase.listofStudent.containsKey(id))
        {
            System.out.println("No Student with entered ID\n");
            return;
        }
        Student temp=LibraryDataBase.listofStudent.get(id);
        if(temp.dues==0)
        {
            System.out.println("Student has no outstanding dues\n");
            return;
        }
        int t=temp.dues;
        temp.dues=0;
        System.out.println("Student dues of Rs. "+t+" settled\n");
        return;
    }
}

class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        LibraryDataBase.readStudentMap("StudentDetails");
        LibraryDataBase.readBookMap("BookDetails");
        LibraryDataBase.readDuesVar("DuesDetails");
        Librarian lib=new Librarian("Admin","1234");
        Scanner sc=new Scanner(System.in);
        System.out.println(" \nWelcome to BITS Library Management System !");
        int sl=0;
        while(sl != 3)
        {
        System.out.println("Press 1 for Student ||  2 for Librarian || 3 to Exit");
        System.out.print("Enter your choice : ");
        sl=sc.nextInt();
        sc.nextLine();
        System.out.println("");
        switch(sl)
        {
            case 1:  //Student Mode
            {
                Student s=null;
                int k1=0;
                while(k1!=1) //For Student Mode
                {
                System.out.println("Register New User: Press 1 || Existing User Login: Press 2");
                System.out.print("Enter your choice: ");
                int lr=sc.nextInt();  
                sc.nextLine();
                System.out.println("");
                switch(lr)  //Login
                {
                    case 1:
                    {
                        System.out.println("Enter your details");
                        System.out.print("Student ID : ");   
                        int uid=sc.nextInt();
                        sc.nextLine();
                        if(LibraryDataBase.listofStudent.containsKey(uid))
                        {
                            System.out.println("User already exists. Please login\n");
                            break;
                        }
                        if(uid==0)
                        {
                            System.out.println("Enter correct value");
                            break;
                        }
                        System.out.print("Student Name : ");
                        String uname=sc.nextLine();
                        System.out.print("Password : ");
                        String upassword=sc.nextLine();
                        System.out.println("");
                        s=new Student(uname,upassword,uid);
                        k1=1;
                        System.out.println("Hello " + s.uname);
                        break;
                    }
                    case 2: //Exisitng Login
                    {
                        System.out.print("Enter your Student ID : ");
                        int uid=sc.nextInt();
                        sc.nextLine();
                        if(!LibraryDataBase.listofStudent.containsKey(uid))
                        {
                            System.out.println("Student ID not found. \n");
                            break;
                        }
                        Student temp=LibraryDataBase.listofStudent.get(uid);
                        System.out.print("Enter your Password : ");
                        String upassword=sc.nextLine();
                        if(!(upassword.equals(temp.password)))
                        {
                            System.out.println("Incorrect Password\n");
                            break;
                        }
                        s=temp;
                        System.out.println("Hello " + s.uname);
                        k1=1;
                        break;
                    }
                    case 3:
                    {
                        
                    }
                    default:
                    {
                        System.out.println("Enter valid choice\n");
                        break;
                    }
                }
                }

                int k2=0;
                while(k2!=7)  //Student Menu
                {
                    System.out.println("Please enter what you wish to do");
                    System.out.println("1. Search Book");
                    System.out.println("2. View Books on Particular Subject");
                    System.out.println("3. Issue Book");
                    System.out.println("4. Renew Book");
                    System.out.println("5. Return Book");
                    System.out.println("6. View Dues");
                    System.out.println("7. Logout");
                    System.out.print("Enter your choice: ");
                    k2=sc.nextInt();
                    sc.nextLine();
                    switch(k2)  //Student Operations
                    {
                        case 1:
                        {
                            System.out.print("Enter Book Title : ");
                            String stitle=sc.nextLine();
                            System.out.print("Enter Book Edition : ");
                            String sedition=sc.nextLine();
                            s.searchBook(stitle,sedition);
                            break;
                        }
                        case 2:
                        {
                            System.out.print("Enter Subject : ");
                            String sub=sc.nextLine();
                            s.viewSubBook(sub);
                            break;
                        }
                        case 3:
                        {
                            System.out.print("Enter book id to issue : ");
                            int iid=sc.nextInt();
                            sc.nextLine();
                            s.issueBook(iid);
                            break;
                        }
                        case 4:
                        {
                            System.out.print("Enter book id to renew : ");
                            int rid=sc.nextInt();
                            sc.nextLine();
                            s.renewBook(rid);
                            break;
                        }
                        case 5:
                        {
                            System.out.print("Enter book id to return : ");
                            int rid=sc.nextInt();
                            sc.nextLine();
                            s.returnBook(rid);
                            break;
                        }
                        case 6:
                        {
                            s.viewDues();
                            break;
                        }
                        case 7:  //Exit out of student mode
                        {
                            System.out.println("Logged out succesfully\n");
                            break;
                        }
                        default:
                        {
                            System.out.println("Enter valid choice");
                            break;
                        }
                    }
                }
                break;
            }
            case 2:    //Admin Mode
            {
                int k3=0;
                while(k3!=1)  //Admin Login
                {
                    System.out.print("Enter librarian password : ");
                    String lpass=sc.nextLine();
                    if(lpass.equals(lib.password))
                    {
                        k3=1;
                        System.out.println("Welcome "+ lib.uname);
                    }
                    else
                    {
                        System.out.println("Wrong Password Entered. Try Again");
                    }
                }
                int k4=0;
                while(k4!=12)  //Librarian Menu
                {
                    System.out.println("Please enter what you wish to do");
                    System.out.println("1. Add Book");
                    System.out.println("2. Remove Book");
                    System.out.println("3. Search Book");
                    System.out.println("4. Edit Book Details");
                    System.out.println("5. View All Books");
                    System.out.println("6. Search Student");
                    System.out.println("7. View All Students");
                    System.out.println("8. Edit Student Details");
                    System.out.println("9. View Library Statistics");
                    System.out.println("10. Settle Student Dues");
                    System.out.println("11. Change Late Fee");
                    System.out.println("12. Logout");
                    System.out.print("Enter your choice: ");
                    k4=sc.nextInt();
                    sc.nextLine();
                    switch(k4)
                    {
                        case 1:  
                        {
                            System.out.println("");
                            System.out.println("Enter Book Details");
                            System.out.print("Enter Book ID: ");
                            int tbid=sc.nextInt();
                            sc.nextLine();
                            if(LibraryDataBase.listofBooks.containsKey(tbid))
                            {
                                System.out.println("Book with entered ID already exists\n");
                                break;
                            }
                            System.out.print("Enter Book Name: ");
                            String tbname=sc.nextLine();
                            System.out.print("Enter Book Author: ");
                            String tbauthor=sc.nextLine();
                            System.out.print("Enter Book Edition: "); 
                            String tbedition=sc.nextLine();
                            System.out.print("Enter Book Subject: ");
                            String tbsubject=sc.nextLine();
                            System.out.print("Enter Book Publisher: ");
                            String tbpublisher=sc.nextLine();
                            int check=0;
                            for(Map.Entry<Integer,Book> entry: LibraryDataBase.listofBooks.entrySet())
                             {
                                Book temp=entry.getValue();
                                if(temp.title.equalsIgnoreCase(tbname) && temp.author.equalsIgnoreCase(tbauthor) && temp.subject.equalsIgnoreCase(tbsubject) && temp.publisher.equalsIgnoreCase(tbpublisher) && temp.edition.equals(tbedition))
                                {
                                    check=1;
                                    System.out.println("Book already exists with ID "+temp.b_id+"\n");
                                    break;
                                }
                            }
                            if(check==1)
                            break;
                            new Book(tbname,tbauthor,tbsubject,tbpublisher,tbedition,tbid);
                            System.out.println("Book added succesfully\n");
                            break;
                        }

                        case 2:
                        {
                            System.out.print("Enter book id to remove: ");
                            int temp=sc.nextInt();
                            sc.nextLine();
                            lib.removeBook(temp);
                            break;
                        }

                        case 3:
                        {
                            System.out.print("Enter Book Title : ");
                            String stitle=sc.nextLine();
                            System.out.print("Enter Book Edition : ");
                            String sedition=sc.nextLine();
                            lib.searchBook(stitle,sedition);
                            break;
                        }

                        case 4:
                        {
                            System.out.print("Enter Book ID: ");
                            int tid=sc.nextInt();
                            sc.nextLine();
                            lib.editBookDetails(tid);
                            break;
                        }

                        case 5:
                        {
                            System.out.println("");
                            lib.displayAllBooks();
                            break;
                        }

                        case 6:
                        {
                            System.out.print("Enter Student ID: ");
                            int temp=sc.nextInt();
                            sc.nextLine();
                            System.out.print("\n");
                            lib.searchStudent(temp);
                            break;
                        }

                        case 7:
                        {
                            System.out.println("");
                            lib.displayAllStudents();
                            break;
                        }
            
                        case 8:
                        {
                            System.out.print("Enter Student ID: ");
                            int temp=sc.nextInt();
                            sc.nextLine();
                            lib.editStudentDetails(temp);
                            break;
                        }

                        case 9:
                        {
                            System.out.println("");
                            System.out.println("Total Students: "+LibraryDataBase.listofStudent.size());
                            System.out.println("Total Books: "+LibraryDataBase.listofBooks.size());
                            int cb=0;
                            for(Map.Entry<Integer,Book> entry: LibraryDataBase.listofBooks.entrySet())
                            {
                                Book temp=entry.getValue();
                                if(temp.status==false)
                                cb++;
                            
                            } 
                            System.out.println("Total Checked Out Books: "+cb);
                            int od=0;
                            for(Map.Entry<Integer,Student> entry: LibraryDataBase.listofStudent.entrySet())
                            {
                                Student temp=entry.getValue();
                                od+=temp.dues;
                            } 
                            System.out.println("Total Outstanding Dues: Rs. "+od+"\n");
                            break;
                        }

                        case 10:
                        {
                            System.out.println("");
                            System.out.print("Enter Student ID: ");
                            int id=sc.nextInt();
                            sc.nextLine();
                            lib.settleDues(id);
                            break;
                        }

                        case 11:
                        {
                            System.out.println("Current Late Fee is Rs. "+ LibraryDataBase.rate + " per day");
                            System.out.print("Enter new fee: Rs. ");
                            int r=sc.nextInt();
                            sc.nextLine();
                            if(r<0)
                            System.out.println("Enter positive value\n");
                            else  
                            {
                                LibraryDataBase.rate=r;
                                System.out.println("Rate updated succesfully\n");
                            }
                            break;
                        }

                        case 12:
                        {
                            System.out.println("Logged out succesfully\n");
                            break;
                        }

                        default:
                        {
                            System.out.println("Enter valid choice\n");
                        }
                    }
                }
                break;
            }

            case 3:   //Final Exit
            {
                LibraryDataBase.writeStudentMap("StudentDetails");
                LibraryDataBase.writeBookMap("BookDetails");
                LibraryDataBase.writeDuesVar("DuesDetails");
                System.out.println("Thank you and have a nice day !! \n");
                break;
            }
            
            default:
            {
                System.out.println("Enter valid input\n");
            }
        }
        }
    }
}