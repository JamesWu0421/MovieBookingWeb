<template>
    <form @submit.prevent="onSubmit">
    <div class="form-row">
      <label>中文片名</label>
      <input v-model="localMovie.title" required />
    </div>
    
    <div class="form-row">
      <label>英文片名</label>
      <input v-model="localMovie.engTitle" />
    </div>

    <div class="form-row">
      <label>分級</label>
      <select v-model="localMovie.ratingLevel" style="height: 30px; border-radius: 3.5px;" >
        <option value="">請選擇分級</option>
        <option value="/images/ic_rating_g.png">普遍級（G）</option>
        <option value="/images/ic_rating_p.png">保護級（PG6）</option>
        <option value="/images/ic_rating_pg12.png">輔導級（PG12）</option>
        <option value="/images/ic_rating_pg15.png">輔導級（PG15）</option>
        <option value="/images/ic_rating_r.png">限制級（R18）</option>
      </select>
    </div>

    <div class="form-row">
      <label>片長（分鐘） </label>
      <input type="number" v-model.number="localMovie.runtimeMinutes" min="1" required />
    </div>

    <div class="form-row">
      <label>上映日</label>
      <input type="date" v-model="localMovie.releaseDate" />
    </div>

    <div class="form-row">
      <label>海報圖片 URL</label>
      <input v-model="localMovie.posterUrl" />
    </div>

    <div class="form-row">
      <label>預告片影片 URL</label>
      <input v-model="localMovie.trailerUrl" />
    </div>

    <div class="form-row">
      <label>導演</label>
      <input v-model="localMovie.director" />
    </div>

    <div class="form-row">
      <label>演員</label>
      <textarea v-model="localMovie.cast" rows="2" />
    </div>

    <!-- 類型 -->
    <div class="form-row">
      <label>類型（可複選）</label>
      <div class="checkbox-group">
        <label class="genres"><input type="checkbox" value="動作" v-model="localMovie.genres" /> 動作</label>
        <label><input type="checkbox" value="冒險" v-model="localMovie.genres" /> 冒險</label>
        <label><input type="checkbox" value="愛情" v-model="localMovie.genres" /> 愛情</label>
        <label><input type="checkbox" value="科幻" v-model="localMovie.genres" /> 科幻</label>
        <label><input type="checkbox" value="驚悚" v-model="localMovie.genres" /> 驚悚</label>
        <label><input type="checkbox" value="恐怖" v-model="localMovie.genres" /> 恐怖</label>
      </div>
      <div class="checkbox-group">
        <label><input type="checkbox" value="喜劇" v-model="localMovie.genres" /> 喜劇</label>
        <label><input type="checkbox" value="劇情" v-model="localMovie.genres" /> 劇情</label>
        <label><input type="checkbox" value="動畫" v-model="localMovie.genres" /> 動畫</label>
        <label><input type="checkbox" value="犯罪" v-model="localMovie.genres" /> 犯罪</label>
        <label><input type="checkbox" value="奇幻" v-model="localMovie.genres" /> 奇幻</label>
      </div>
      <p v-if="localMovie.genres && localMovie.genres.length > 0" class="selected-genres">
        {{ localMovie.genres.join('，') }}
      </p>
    </div>

    <div class="form-row">
      <label>電影介紹</label>
      <textarea v-model="localMovie.description" rows="4" />
    </div>

    <div class="form-row">
      <label>搜尋關鍵字</label>
      <textarea v-model="localMovie.keywords" rows="2" />
    </div>

    <div class="form-row">
      <label>
        <input type="checkbox" v-model="localMovie.isPublished" />
        是否上架
      </label>
    </div>

    <div class="form-actions">
      <button type="submit" class="action-btn save">{{ submitText }}</button>
      <button type="button" class="action-btn cancel" @click="$emit('cancel')">取消</button>
    </div>
  </form>
</template>

<script setup>
import { reactive, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({}),
  },
  submitText: {
    type: String,
    default: '儲存',
  },
});


const emit = defineEmits(['update:modelValue', 'submit', 'cancel']);

// 表單用的本地狀態
const localMovie = reactive({
  id: null,
  title: '',
  engTitle: '',
  ratingLevel: '',
  runtimeMinutes: 90,
  releaseDate: '',
  posterUrl: '',
  trailerUrl: '',
  director: '',
  cast: '',
  description: '',
  isPublished: false,
  genres: [],
  keywords: '',
});

// 同步外面的 modelValue 進來（編輯舊資料用）
watch(
  () => props.modelValue,
  (val) => {
    Object.assign(localMovie, {
      id: val?.id ?? null,
      title: val?.title ?? '',
      engTitle: val?.engTitle ?? '',
      ratingLevel: val?.ratingLevel ?? '',
      runtimeMinutes: val?.runtimeMinutes ?? 90,
      releaseDate: val?.releaseDate ?? '',
      posterUrl: val?.posterUrl ?? '',
      trailerUrl: val?.trailerUrl ?? '',
      director: val?.director ?? '',
      cast: val?.cast ?? '',
      description: val?.description ?? '',
      isPublished: val?.isPublished ?? false,
      keywords: val?.keywords ?? '',
      genres: Array.isArray(val?.genres)
        ? val.genres
        : (val?.genres
            ? String(val.genres)
                .split(',')
                .map(g => g.trim())
                .filter(Boolean)
            : []),
    });
  },
  { immediate: true, deep: true },
);

// 送出表單
const onSubmit = () => {
  const data = { ...localMovie };
  data.genres = Array.isArray(localMovie.genres)
    ? localMovie.genres.join(',')
    : localMovie.genres;

  emit('update:modelValue', data);
  emit('submit', data);
};
</script>

<style scoped>
.form-row {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.form-row label {
  font-weight: 500;
  margin-bottom: 4px;
}
.form-row input,
.form-row textarea {
  border: 1px solid #ccc;
  padding: 6px 8px;
  border-radius: 4px;
}
.form-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.checkbox-group {
  /* display: flex; */
  flex-wrap: wrap;
  gap: 16px 16px;
  margin: 10px 0;
}

.selected-genres {
  margin: 6px;
  font-size: 14px;
  height: 30px;
  border: 1px solid #ccc;
  border-radius: 3px;
}

.action-btn {
  padding: 6px 14px;
  border-radius: 5px;
  border: 1px solid #ccc;
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
  background-color: #fff;
  margin-right: 8px;
}

.action-btn.save{
  font-weight: 400;
  letter-spacing: 2px;
  background-color: rgba(116, 175, 231, 0.5);
  color: #2980bf;
  border-color: #3fa5ee;
}

.action-btn.save:hover {
  background-color: #3683d0;
  color: #ffffff;
}

.action-btn.cancel {
  background-color: rgba(231, 125, 141, 0.5);
  color: #cd4949;
  border-color:#e98787;
}

.action-btn.cancel:hover {
  background-color: #d45f5f;
  color: #ffffff;
}

</style>
