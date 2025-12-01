// package tw.com.ispan.controller;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import tw.com.ispan.domain.BookBean;
// import tw.com.ispan.service.BookService;

// @RestController
// @RequestMapping("/api/books") // 統一路徑開頭
// public class BookController {

//     @Autowired
//     private BookService bookService;

//     // 取得所有書籍
//     @GetMapping
//     public ResponseEntity<List<BookBean>> getAllBooks() {
//         try {
//             List<BookBean> booklist = bookService.getAllBooks();
//             if (booklist.isEmpty()) {
//                 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//             }
//             return new ResponseEntity<>(booklist, HttpStatus.OK);
//         } catch (Exception ex) {
//             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }

//     // 依ID取得書籍
//     @GetMapping("/{id}")
//     public ResponseEntity<BookBean> getBookById(@PathVariable Long id) {
//         Optional<BookBean> bookData = bookService.getBookById(id);
//         return bookData.map(book -> new ResponseEntity<>(book, HttpStatus.OK))
//         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//     }

//     // 新增書籍
//     @PostMapping
//     public ResponseEntity<BookBean> addBook(@RequestBody BookBean book) {
//         try {
//             BookBean newBook = bookService.addBook(book);
//             return new ResponseEntity<>(newBook, HttpStatus.CREATED);
//         } catch (Exception e) {
//             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }

//     // 更新書籍
//     @PutMapping("/{id}")
//     public ResponseEntity<BookBean> updateBookById(@PathVariable Long id, @RequestBody BookBean newBookData) {
//         Optional<BookBean> updated = bookService.updateBookById(id, newBookData);
//         return updated.map(book -> new ResponseEntity<>(book, HttpStatus.OK))
//         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//     }

//     // 刪除書籍
//     @DeleteMapping("/{id}")
//     public ResponseEntity<HttpStatus> deleteBookById(@PathVariable Long id) {
//         boolean deleted = bookService.deleteBookById(id);
//         return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
//         : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//     }
// }
