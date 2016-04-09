# CBMERJ RetiredFinderBot

CBMERJ RetiredFinderBot is a bot application that finds automatically an information inside a daily bulletin of Rio de Janeiro Fireman's website.

# Motivation
The motivation to create this bot is really simple, to help a friend who is waiting for the official note of his retirement. These informations, about retirements and others issues are shown inside a daily fireman's bulletin.

# Use Case

This section describes the use case of this bot. We have some steps to do. The main goal is find the **unique registration number (RG)** of the fireman, is this case, for questions of privacity, lets use **12.345** as a valid **RG**. So, we must get the daily bulletin and search for **12.345**. The steps are shown below.
  - Get the [fireman's web site][rjfws]
  - Log in with credentials
  - Get the bulletins page of the current year
  - Select the current month link
  - Get the bulletins (stream) separated by day (PDF files)
  - Search for **12.345** number inside a collected bulletin
  - Send an email with the results

> The fireman's web site was not hacked, to log in was used a real credential.

### Version
1.0

### Dependencies

CBMERJ RetiredFinderBot application require some *third-party libraries*, these dependencies are listed next:

* [Selenium] - A web browser automation tool
* [Apache PDFBox] - An open source Java tool for working with PDF documents
* [Java Mail] - A Java API used to send and receive email via SMTP, POP3 and IMAP.

### SpringBoot Utilization

This application was made using [SpringBoot], the initial idea to use springboot was to deploy easily on a server. Furthermore was used another cool feature, the capacity of scheduling tasks. So was used a [Cron] expression to schedule a task to check a new bulletin. The cron expression is :

> At 12:00 and 21:00 on Mon, Tue, Wed, Thu and Fri.

### Development

This project development is stopped, because the main goal was reached, the retirement was published, and was found by this application ;)

License
----

MIT


   [rjfws]:<http://www.cbmerj.rj.gov.br>
   [Selenium]:<http://www.seleniumhq.org/>
   [Apache PDFBox]:<https://pdfbox.apache.org/>
   [Java Mail]:<http://www.oracle.com/technetwork/java/javamail/index.html>
   [SpringBoot]:<http://projects.spring.io/spring-boot/>
   [Cron]:<https://en.wikipedia.org/wiki/Cron>

