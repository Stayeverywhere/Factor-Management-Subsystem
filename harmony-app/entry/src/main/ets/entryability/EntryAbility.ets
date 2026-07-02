import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import { window } from '@kit.ArkUI';
import { BusinessError } from '@kit.BasicServicesKit';

export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    console.info('FactorApp EntryAbility onCreate');
  }

  onDestroy(): void {
    console.info('FactorApp EntryAbility onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    windowStage.loadContent('pages/Index', (err: BusinessError) => {
      if (err.code) {
        console.error('Failed to load content. Code: ' + err.code + ', msg: ' + err.message);
        return;
      }
      console.info('FactorApp page loaded');
    });
  }

  onWindowStageDestroy(): void {
    console.info('FactorApp EntryAbility onWindowStageDestroy');
  }

  onForeground(): void {
    console.info('FactorApp EntryAbility onForeground');
  }

  onBackground(): void {
    console.info('FactorApp EntryAbility onBackground');
  }
}
