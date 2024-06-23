# DB_to_Excel

ExcelCreateService version2 - Create instances of IMG and add them to the "last" position of your Dto/Command class.
Place the field name containing the image URL as a parameter in the IMG constructor. Afterward, use the method with HttpServletResponse, String Filename, List<?> DATA, int number_of_images, img_width, and img_height. 
This method will create an Excel file based on your data and image URLs.
Be aware that errors may occur during this process.
