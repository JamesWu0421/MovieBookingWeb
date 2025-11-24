// package tw.com.ispan.service;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import jakarta.transaction.Transactional;
// import tw.com.ispan.domain.BookBean;
// import tw.com.ispan.repository.Bookrepository;

// @Service
// @Transactional
// public class BookService {

//     @Autowired
//     private Bookrepository bookRepo;

//     // 取得全部書籍
//     public List<BookBean> getAllBooks() {
//         List<BookBean> booklist = new ArrayList<>();
//         bookRepo.findAll().forEach(booklist::add);
//         return booklist;
//     }

//     // 依 ID 查詢書籍
//     public Optional<BookBean> getBookById(Long id) {
//         return bookRepo.findById(id);
//     }

//     // 新增書籍
//     public BookBean addBook(BookBean book) {
//         return bookRepo.save(book);
//     }

//     // 更新書籍
//     public Optional<BookBean> updateBookById(Long id, BookBean newBookData) {
//         Optional<BookBean> oldBookData = bookRepo.findById(id);
//         if (oldBookData.isPresent()) {
//             BookBean updatedBook = oldBookData.get();
//             updatedBook.setTitle(newBookData.getTitle());
//             updatedBook.setAuthor(newBookData.getAuthor());
//             return Optional.of(bookRepo.save(updatedBook));
//         }
//         return Optional.empty();
//     }

//     // 刪除書籍
//     public boolean deleteBookById(Long id) {
//         if (bookRepo.existsById(id)) {
//             bookRepo.deleteById(id);
//             return true;
//         }
//         return false;
//     }
// }
