# Compare and swap (CAS) : So sánh và tráo đổi

**CAS** là một kỹ thuật tối ưu hóa trong lập trình đa luồng (Multithreading), cho phép cập nhật dữ liệu một cách an toàn mà không cần dùng đến cơ chế khóa truyền thống (Lock-free).

---

## 1. Ý tưởng cốt lõi (Bộ 3 Tham Số)

Một thao tác CAS luôn hoạt động dựa trên 3 tham số:

1. **`V` (Value / Memory Location):** Địa chỉ ô nhớ chứa giá trị cần cập nhật.
2. **`A` (Expected Value):** Giá trị kỳ vọng (giá trị mà luồng _nghĩ_ là đang nằm ở ô nhớ $V$).
3. **`B` (New Value):** Giá trị mới muốn ghi đè vào $V$.

> **Nguyên lý:** Nếu giá trị hiện tại tại $V$ bằng $A$, hệ thống lập tức ghi đè $B$ vào $V$ và trả về `true`. Ngược lại, nếu giá trị tại $V$ đã bị luồng khác sửa đổi (khác $A$), thao tác thất bại, trả về `false` và không ghi đè.

---

## 2. Bản chất ở tầng phần cứng: Lệnh `cmpxchg`

CAS không phải là một thuật toán phần mềm chạy qua nhiều bước xử lý tuần tự. Ở tầng thấp nhất, nó được thực hiện bằng một lệnh hợp ngữ (Assembly) duy nhất của CPU: **`cmpxchg` (Compare and Exchange)**.

- Khi đi kèm tiền tố `LOCK` (`LOCK CMPXCHG`), CPU sẽ **khóa tầng vật lý (Bus Lock hoặc Cache Lock)** đối với ô nhớ đó.
- Quá trình _So sánh -> Tráo đổi_ diễn ra hoàn toàn cô lập (Atomic - Nguyên tử), không một luồng hay lõi CPU nào khác có thể xen vào giữa.
- Thời gian thực thi chỉ tốn vài chu kỳ xung nhịp CPU, nhanh hơn rất nhiều so với Lock phần mềm.

---

## 3. CAS với Biến Nguyên Thủy (Primitive) vs Biến Tham Chiếu (Reference)

| Tiêu chí            | Biến Nguyên Thủy (int, long...)          | Biến Tham Chiếu (Pointer / Reference)                                                                             |
| :------------------ | :--------------------------------------- | :---------------------------------------------------------------------------------------------------------------- |
| **Giá trị của `V`** | Là giá trị số học cụ thể (ví dụ: `100`). | Là **địa chỉ vùng nhớ** của Object (ví dụ: `0x7fff`).                                                             |
| **Cơ chế so sánh**  | So sánh bằng về mặt số học.              | So sánh xem 2 biến có **cùng trỏ vào 1 địa chỉ** hay không (`==`).                                                |
| **Hành vi**         | Thay đổi giá trị số học.                 | Thay đổi đích trỏ của tham chiếu sang Object khác.                                                                |
| **Lưu ý đặc biệt**  | Trực quan, ít lỗi.                       | Nếu thuộc tính bên trong Object thay đổi nhưng địa chỉ Object không đổi $\rightarrow$ **CAS vẫn báo THÀNH CÔNG**. |

---

## 4. Mô phỏng CAS bằng Code (Mã giả Java)

### Vòng lặp CAS (Spin-lock tự chế để tăng biến đếm không dùng Lock)

```java
public class SimulatedAtomicCounter {
    // V: Ô nhớ chứa giá trị thực tế
    private volatile int value = 0;

    // Mô phỏng lệnh cmpxchg của CPU bằng phần mềm
    public synchronized boolean compareAndSwap(int expectedValue, int newValue) {
        if (this.value == expectedValue) {
            this.value = newValue;
            return true; // Thành công
        }
        return false; // Thất bại
    }

    // Hàm tăng giá trị (Lock-free)
    public void increment() {
        int currentValue;
        do {
            // Bước 1: Đọc giá trị hiện tại (Đóng vai trò là Expected Value 'A')
            currentValue = this.value;

            // Bước 2: Thử cập nhật bằng CAS.
            // Nếu có luồng khác chen ngang làm thay đổi 'value', CAS trả về false.
            // Vòng lặp do-while sẽ bắt buộc luồng phải đọc lại và thử lại.
        } while (!compareAndSwap(currentValue, currentValue + 1));
    }

    public int getValue() {
        return value;
    }
}


```
## 5. Ưu điểm & Nhược điểm của CAS

### Ưu điểm
* **Hiệu năng cao (High Throughput):** Tránh được chi phí cực lớn của việc `Context Switch` (giai đoạn CPU phải ngủ đông luồng này và đánh thức luồng khác dậy).
* **Không bị Deadlock:** Vì không có luồng nào chiếm giữ độc quyền tài nguyên nên hoàn toàn không xảy ra tình trạng khóa vòng lặp chéo nhau.

---

### Nhược điểm & Cách khắc phục

#### 1. Vấn đề ABA (ABA Problem)
* **Hiện tượng:** Luồng 1 đọc giá trị ban đầu là `A`. Luồng 2 nhảy vào sửa `A` thành `B`, rồi ngay sau đó lại sửa `B` ngược về `A`. Khi luồng 1 quay lại thực hiện CAS, nó thấy giá trị vẫn là `A` (đúng như kỳ vọng) nên cho qua và cập nhật. Tuy nhiên, thực tế dữ liệu đã bị biến đổi ở giữa quá trình.
* **Cách khắc phục:** Gắn thêm **Số phiên bản (Version)** hoặc **Nhãn thời gian (Timestamp)** đi kèm dữ liệu. 
  * *Ví dụ:* Trong Java, ta dùng `AtomicStampedReference`. Mỗi lần cập nhật, số phiên bản sẽ tăng lên 1 (Ví dụ chuyển đổi: `A1 -> B2 -> A3`). Lúc này, Luồng 1 kiểm tra thấy phiên bản kỳ vọng ban đầu là `1` nhưng hiện tại đã là `3` (`A1 != A3`) nên sẽ phát hiện ra sự thay đổi và ngăn chặn kịp thời.

#### 2. Tốn CPU khi tranh chấp cao (Busy Spin)
* **Hiện tượng:** Nếu có hàng trăm luồng cùng lao vào cập nhật một biến duy nhất, vòng lặp `do-while` (thử lại) của các luồng thất bại sẽ phải chạy liên tục (gọi là *Spinning*). Điều này khiến CPU bị đẩy lên 100% công suất một cách vô ích chỉ để đứng chờ xếp hàng.
* **Cách khắc phục:** * Khi tỷ lệ tranh chấp quá cao, hãy cân nhắc quay lại dùng các cơ chế Khóa truyền thống như `synchronized` hoặc `ReentrantLock` (để đưa luồng thất bại vào trạng thái ngủ thay vì bắt nó chạy vòng lặp vô tận).
  * Hoặc sử dụng các cấu trúc dữ liệu phân tán thông minh như `LongAdder` trong Java (cơ chế này chia nhỏ biến đếm thành nhiều ô nhớ phụ để các luồng không bị đâm sầm vào cùng một chỗ, sau đó mới cộng dồn lại).


## 6. Tổng hợp các tình huống phỏng vấn thực chiến (Q&A)

### Câu hỏi 1: Xử lý biến đếm đồng thời (Race Condition)
* **Tình huống:** Hệ thống có biến `private int viewCount = 0;`. Có 1000 luồng cùng lao vào tăng biến đếm (`viewCount++`) tại cùng một thời điểm.
* **Hiện tượng:** Kết quả cuối cùng sẽ **nhỏ hơn 1000**. Nguyên nhân do `viewCount++` không phải là toán tử nguyên tử (Atomic). Các luồng sẽ đọc đè giá trị lên nhau (Race Condition).
* **Giải pháp Lock-free:** Thay vì dùng `synchronized` (làm chậm hệ thống), ta bọc biến lại bằng **`AtomicInteger`** để tận dụng giải pháp tăng an toàn bằng cấu trúc CAS ở tầng dưới.

---

### Câu hỏi 2: Giới hạn của CAS khi tranh chấp cực cao (High Contention)
* **Tình huống:** Nếu hệ thống quá hot, có **100.000 đến 1.000.000 luồng** cùng lao vào tăng biến đếm của `AtomicInteger` cùng một lúc.
* **Vấn đề (Nghẽn cổ chai):** CPU sẽ bị **đẩy lên 100% công suất** một cách vô ích. Bản chất các luồng chạy hoàn toàn đồng thời và tranh giành nhau khốc liệt qua lệnh phần cứng `LOCK CMPXCHG`. Sẽ chỉ có duy nhất 1 luồng may mắn chiếm được ô nhớ thành công (mở khóa và ghi đè), 99.999 luồng còn lại bị báo *Thất bại (Fail)*. Theo cơ chế vòng lặp `do-while`, 99.999 luồng này lập tức quay xe, đọc lại và húc tiếp liên tục (Busy Spin / Spinning). Số lượng luồng chờ tăng lũy tiến khiến CPU quá tải.
* **Giải pháp tối ưu:** Sử dụng **`LongAdder`** (từ Java 8). Thay vì bắt tất cả lao vào 1 ô nhớ, `LongAdder` tự động phân tán biến đếm thành một **mảng gồm nhiều ô nhớ phụ (Cell)**. Các luồng sẽ phân tán vào các Cell khác nhau để giảm tỷ lệ chọi (giảm CAS fail). Cuối cùng, khi cần lấy kết quả chỉ cần gọi hàm `.sum()` để cộng dồn các ô lại.
* **Lưu ý đặc biệt:** `LongAdder` chỉ dùng cho các bài toán **Đếm tích lũy độc lập** (chỉ cộng/trừ). Đối với các bài toán cần nhìn thấy một con số tổng duy nhất tại một thời điểm để đưa ra quyết định (ví dụ: *Rút tiền tài khoản ngân hàng* - cần check xem tổng số dư có đủ không rồi mới trừ), **không được dùng `LongAdder`** mà bắt buộc phải dùng biến tập trung dạng `Atomic` hoặc xử lý bằng **Database Transaction** (Optimistic/Pessimistic Lock) ở tầng DB.

---

### Câu hỏi 3: Lỗi ABA với Biến Tham Chiếu (Reference)
* **Tình huống:** Ban đầu Object `User.name = "Mạnh"`. Luồng 1 dùng `AtomicReference` lưu địa chỉ vùng nhớ của Object này (ví dụ: `0x111`). Luồng 2 nhảy vào sửa thuộc tính bên trong thành `"Duy"`, rồi lại sửa ngược về `"Mạnh"`. Địa chỉ vùng nhớ của Object vẫn giữ nguyên là `0x111`.
* **Kết quả lệnh CAS:** Luồng 1 quay lại thực hiện CAS với giá trị kỳ vọng ban đầu sẽ trả về **`true` (Thành công)**.
* **Bản chất phần cứng:** Lệnh `cmpxchg` đối với biến tham chiếu chỉ so sánh bit-by-bit giá trị lưu tại ô nhớ của biến đó (tức là so sánh 2 địa chỉ vùng nhớ xem có trỏ cùng vào một chỗ không: `0x111 == 0x111`). Vì địa chỉ không đổi nên CPU coi như chưa có gì xảy ra.
* **Mối nguy hại & Cách khắc phục:** Dữ liệu thực tế bên trong đã bị biến đổi nhưng hệ thống không phát hiện được. Để giải quyết, khi làm việc với CAS cho Object, nên thiết kế Object đó ở dạng **Immutable (Không thể chỉnh sửa thuộc tính)**. Mỗi lần muốn thay đổi dữ liệu, bắt buộc phải tạo (`new`) một Object mới để thay đổi hẳn địa chỉ vùng nhớ, hoặc sử dụng cơ chế gắn số phiên bản (Version) như `AtomicStampedReference`.

---

## 7. Giải mã cơ chế vòng lặp `do-while` trong thuật toán CAS

Một thắc mắc kinh điển: *"Tại sao lần đầu tiên chạy vào `do-while`, các luồng không cần check điều kiện `while` mà vẫn chỉ có 1 luồng thành công? Phải chăng tụi nó chạy tuần tự?"*

### Bản chất cơ chế vận hành:
1. **Không chạy tuần tự:** Tất cả các luồng lao vào thân vòng lặp `do` hoàn toàn đồng thời (song song) trên các lõi CPU khác nhau.
2. **Bị chặn ở dòng lệnh CAS:** Lần đầu tiên chạy, tất cả các luồng đều vượt qua vòng gửi xe và thực hiện dòng lệnh `currentValue = this.value;` (đều đọc được giá trị cũ, ví dụ là `0`). Nhưng ngay bước tiếp theo khi gọi lệnh CAS, **CPU sẽ làm trọng tài thông qua lệnh `LOCK CMPXCHG`**:
   * Tại một thời điểm cực ngắn, CPU chỉ cho phép **duy nhất 1 luồng nhanh nhất** chiếm quyền xử lý ô nhớ. Luồng này so sánh thấy `0 == 0` nên ghi đè thành `1` thành công $\rightarrow$ Hàm CAS trả về `true` $\rightarrow$ Điều kiện `while(!true)` thành `while(false)` $\rightarrow$ **Luồng này thoát vòng lặp**.
   * Toàn bộ các luồng còn lại bị khựng lại một vài chu kỳ xung nhịp. Khi được vào so sánh, tụi nó thấy giá trị thực tế trong ô nhớ đã bị đứa đi trước sửa thành `1` rồi (khác với giá trị `0` tụi nó đọc ban đầu) $\rightarrow$ Hàm CAS trả về `false`.
3. **Giá trị kỳ vọng $A$ thay đổi thế nào ở các lượt lặp sau?**
   Do hàm CAS trả về `false`, điều kiện `while(!false)` trở thành `while(true)`. Toàn bộ các luồng thất bại bị ép phải **bắt đầu lượt lặp thứ 2**. Lúc này, tụi nó quay lại dòng đầu tiên của thân vòng lặp: `currentValue = this.value;`. Dòng này giúp các luồng **đọc lại giá trị mới nhất vừa bị cập nhật trong bộ nhớ (lúc này là `1`)**. Giá trị kỳ vọng $A$ được cập nhật mới từ đây, giúp luồng sẵn sàng cho một cuộc đua (lượt lặp) tiếp theo.

# To learn later
- Cách biến được lưu trong RAM (cpu)
