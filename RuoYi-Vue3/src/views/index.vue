<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="hero-title">数据清洗平台</h1>
        <p class="hero-desc">高效、智能的数据处理与管理系统</p>
      </div>
    </div>

    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 003 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z" stroke="currentColor" stroke-width="2"/>
            <polyline points="3.27,6.96 12,12.01 20.73,6.96" stroke="currentColor" stroke-width="2"/>
            <line x1="12" y1="22.08" x2="12" y2="12" stroke="currentColor" stroke-width="2"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.dataSources }}</span>
          <span class="stat-label">数据源</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 20h9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <path d="M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.tasks }}</span>
          <span class="stat-label">清洗任务</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M22 11.08V12a10 10 0 11-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <polyline points="22,4 12,14.01 9,11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.records }}</span>
          <span class="stat-label">处理记录</span>
        </div>
      </div>
    </div>

    <div class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <polyline points="17,8 12,3 7,8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="12" y1="3" x2="12" y2="15" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h3>数据导入导出</h3>
          <p>支持多种格式数据批量导入，灵活导出清洗结果</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
              <line x1="21" y1="21" x2="16.65" y2="16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3>数据质量检测</h3>
          <p>智能识别脏数据，空值、重复、格式错误一览无余</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <polyline points="16,3 21,3 21,8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="4" y1="20" x2="21" y2="3" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <polyline points="21,16 21,21 16,21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="15" y1="15" x2="21" y2="21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="4" y1="4" x2="9" y2="9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3>数据转换</h3>
          <p>灵活配置转换规则，批量处理数据格式与内容</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
              <line x1="3" y1="9" x2="21" y2="9" stroke="currentColor" stroke-width="2"/>
              <line x1="9" y1="21" x2="9" y2="9" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h3>任务调度</h3>
          <p>定时执行清洗任务，自动化数据处理流程</p>
        </div>
      </div>
    </div>

    <div class="version-info">
      <span>v1.0.0</span>
    </div>
  </div>
</template>

<script setup>
const stats = ref({
  dataSources: 12,
  tasks: 156,
  records: 2847
})
</script>

<style scoped lang="scss">
.home-container {
  padding: 32px;
  min-height: calc(100vh - 120px);
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.hero-section {
  text-align: center;
  padding: 48px 24px;
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.08) 0%, rgba(167, 139, 250, 0.08) 100%);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  margin-bottom: 32px;
}

.hero-content {
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.hero-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 20px 40px rgba(6, 182, 212, 0.3);
  animation: pulse 3s ease-in-out infinite;

  svg {
    width: 44px;
    height: 44px;
    color: white;
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.hero-title {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.hero-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
  letter-spacing: 1px;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease-out backwards;

  &:nth-child(1) { animation-delay: 0.1s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.3s; }

  &:hover {
    transform: translateY(-4px);
    border-color: rgba(6, 182, 212, 0.3);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.stat-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.2) 0%, rgba(167, 139, 250, 0.2) 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 28px;
    height: 28px;
    color: #06b6d4;
  }
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 4px;
}

.features-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 20px;
  padding-left: 12px;
  border-left: 3px solid #06b6d4;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.feature-card {
  padding: 24px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease-out backwards;

  &:nth-child(1) { animation-delay: 0.2s; }
  &:nth-child(2) { animation-delay: 0.3s; }
  &:nth-child(3) { animation-delay: 0.4s; }
  &:nth-child(4) { animation-delay: 0.5s; }

  &:hover {
    transform: translateY(-4px);
    border-color: rgba(6, 182, 212, 0.3);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
    background: rgba(255, 255, 255, 0.05);
  }

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #ffffff;
    margin: 16px 0 8px;
  }

  p {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
    margin: 0;
    line-height: 1.6;
  }
}

.feature-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 24px;
    height: 24px;
    color: white;
  }
}

.version-info {
  text-align: center;
  padding: 16px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);

  span {
    padding: 6px 16px;
    background: rgba(255, 255, 255, 0.03);
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.06);
  }
}

@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: 24px;
  }
}
</style>
